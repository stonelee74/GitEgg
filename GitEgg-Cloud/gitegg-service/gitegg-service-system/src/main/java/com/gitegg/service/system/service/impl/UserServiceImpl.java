package com.gitegg.service.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitegg.platform.base.constant.AuthConstant;
import com.gitegg.platform.base.constant.GitEggConstant;
import com.gitegg.platform.base.enums.ResultCodeEnum;
import com.gitegg.platform.base.exception.BusinessException;
import com.gitegg.platform.base.util.BeanCopierUtils;
import com.gitegg.platform.mybatis.enums.DataPermissionTypeEnum;
import com.gitegg.service.system.bo.UserExportBO;
import com.gitegg.service.system.bo.UserImportBO;
import com.gitegg.service.system.dto.*;
import com.gitegg.service.system.entity.*;
import com.gitegg.service.system.enums.ResourceEnum;
import com.gitegg.service.system.mapper.UserMapper;
import com.gitegg.service.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gitegg
 * @ClassName: UserServiceImpl
 * @Description: 用户相关操作接口实现类
 * @date 2018年5月18日 下午3:20:30
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    private final IUserRoleService userRoleService;

    private final IOrganizationUserService organizationUserService;

    private final IDataPermissionUserService dataPermissionUserService;

    private final IResourceService resourceService;

    private final RedisTemplate redisTemplate;

    @Value("${system.defaultPwd}")
    private String defaultPwd;

    @Value("${system.defaultRoleId}")
    private Long defaultRoleId;

    @Value("${system.defaultOrgId}")
    private Long defaultOrgId;

    @Value("${system.defaultUserStatus}")
    private int defaultUserStatus;

    @Value("${system.defaultPwdChangeFirst}")
    private boolean defaultPwdChangeFirst;

    /**
     * 分页查询用户
     *
     * @param user
     * @return
     */
    @Override
    public Page<UserInfo> queryUserPage(Page<UserInfo> page, QueryUserDTO user) {
        Page<UserInfo> pageUserInfo = userMapper.queryUserPage(page, user);
        return pageUserInfo;
    }

    /**
     * 批量查询用户
     *
     * @param user
     * @return
     */
    @Override
    public List<UserInfo> queryUserList(QueryUserDTO user) {
        List<UserInfo> userInfoList = userMapper.queryUserList(user);
        return userInfoList;
    }

    /**
     * 导出用户列表
     *
     * @param user
     * @return
     */
    @Override
    public List<UserExportBO> exportUserList(QueryUserDTO user) {
        List<UserExportBO> userExportList = new ArrayList<>();
        List<UserInfo> userInfoList = this.queryUserList(user);
        if (!CollectionUtils.isEmpty(userInfoList)) {
            for (UserInfo userInfo : userInfoList) {
                UserExportBO userExportBO = BeanCopierUtils.copyByClass(userInfo, UserExportBO.class);
                userExportList.add(userExportBO);
            }
        }
        return userExportList;
    }

    /**
     * 导入用户列表
     *
     * @param file
     * @return
     */
    @Override
    public boolean importUserList(MultipartFile file) {
        boolean importSuccess = false;
        try {
            List<UserImportBO> userImportList = EasyExcel.read(file.getInputStream(), UserImportBO.class, null).sheet().doReadSync();
            if (!CollectionUtils.isEmpty(userImportList)) {
                List<User> userList = new ArrayList<>();
                userImportList.stream().forEach(userImportBO -> {
                    userList.add(BeanCopierUtils.copyByClass(userImportBO, User.class));
                });
                importSuccess = this.saveBatch(userList);
            }
        } catch (IOException e) {
            log.error("批量导入用户数据时发生错误:{}", e);
            throw new BusinessException("批量导入失败:" + e);
        }
        return importSuccess;
    }

    /**
     * 新增用户，成功后将id和对象返回
     *
     * @param user
     * @return
     */
    @Override
    public CreateUserDTO createUser(CreateUserDTO user) {
        User userEntity = BeanCopierUtils.copyByClass(user, User.class);
        // 查询已存在的用户，用户名、昵称、邮箱、手机号有任一重复即视为用户已存在，真实姓名是可以重复的。
        List<User> userList = userMapper.queryExistUser(userEntity);
        if (!CollectionUtils.isEmpty(userList)) {
            throw new BusinessException("账号已经存在");
        }

        // 如果为空时设置默认角色
        Long roleId = user.getRoleId();
        if (null == roleId) {
            // 默认值，改成配置
            roleId = defaultRoleId;
        }

        // 设置默认状态
        if (null == userEntity.getStatus()) {
            userEntity.setStatus(defaultUserStatus);
        }

        // 处理前端传过来的省市区
        userEntity = resolveAreas(userEntity, user.getAreas());

        String pwd = userEntity.getPassword();
        if (StringUtils.isEmpty(pwd)) {
            // 默认密码，配置文件配置
            pwd = defaultPwd;
            // 初次登录需要修改密码
            if (defaultPwdChangeFirst) {
                userEntity.setStatus(GitEggConstant.UserStatus.NOT_ACTIVE);
            }
        }
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String cryptPwd = passwordEncoder.encode(AuthConstant.BCRYPT + userEntity.getAccount() + DigestUtils.md5DigestAsHex(pwd.getBytes()));
        userEntity.setPassword(cryptPwd);

        // 保存用户
        boolean result = this.save(userEntity);
        if (result) {
            // 保存用户和组织机构的关系
            Long organizationId = null == user.getOrganizationId() ? defaultOrgId : user.getOrganizationId();
            OrganizationUser oUser = new OrganizationUser();
            oUser.setUserId(userEntity.getId());
            oUser.setOrganizationId(organizationId);
            oUser.setRoleId(roleId);
            oUser.setIsPrimary(user.getIsPrimary());
            organizationUserService.save(oUser);

            // 返回 ID
            user.setId(userEntity.getId());
        }
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    public boolean updateUser(UpdateUserDTO user) {
        Long orgUserId = user.getOrganizationUserId();
        Long userId = user.getId();
        int isPrimary = user.getIsPrimary();

        if (isPrimary == 1) {
            // 如果本次设置的是主岗，则将其他岗位全部改为兼职
            LambdaUpdateWrapper<OrganizationUser> uw = new LambdaUpdateWrapper<>();
            uw.set(OrganizationUser::getIsPrimary, 0)
                    .eq(OrganizationUser::getUserId, userId);
            organizationUserService.update(uw);
        }

        if (orgUserId != null) {
            OrganizationUser orgUser = organizationUserService.getById(orgUserId);
            if (orgUser == null) {
                throw new BusinessException("无法找到指定记录");
            }

            User userEntity = BeanCopierUtils.copyByClass(user, User.class);
            // 查询已存在的用户，用户名、昵称、邮箱、手机号有任一重复即视为用户已存在，真实姓名是可以重复的。
            List<User> userList = userMapper.queryExistUser(userEntity);
            if (!CollectionUtils.isEmpty(userList)) {
                throw new BusinessException("账号已经存在");
            }

            // 判断是否重复
            LambdaQueryWrapper<OrganizationUser> qw = new LambdaQueryWrapper<>();
            qw.eq(OrganizationUser::getOrganizationId, user.getOrganizationId())
                    .ne(OrganizationUser::getId, orgUserId)
                    .eq(OrganizationUser::getUserId, userEntity.getId())
                    .eq(OrganizationUser::getRoleId, user.getRoleId());
            // 如果这个角色不存在，则删除其他角色，保存这个角色
            if (organizationUserService.count(qw) > 0) {
                throw new BusinessException("岗位配置重复");
            }

            // 处理前端传过来的省市区
            userEntity = resolveAreas(userEntity, user.getAreas());

            String pwd = userEntity.getPassword();
            User oldInfo = this.getById(userEntity.getId());

            if (oldInfo == null) {
                throw new BusinessException("用户未找到");
            }

            if (!StringUtils.isEmpty(pwd)) {
                PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                String cryptPwd = passwordEncoder.encode(AuthConstant.BCRYPT + oldInfo.getAccount() + DigestUtils.md5DigestAsHex(pwd.getBytes()));
                userEntity.setPassword(cryptPwd);
            }

            // 保存用户信息
            boolean result = this.updateById(userEntity);
            if (result) {
                // 修改用户岗位信息
                orgUser.setOrganizationId(user.getOrganizationId());
                orgUser.setUserId(userEntity.getId());
                orgUser.setRoleId(user.getRoleId());
                orgUser.setIsPrimary(isPrimary);

                // 保存岗位信息
                result = organizationUserService.updateById(orgUser);
            }
            return result;
        }

        if (userId != null) {
            // 如果指定用户ID，则执行添加现有员工兼职岗位功能
            User userEntity = this.getById(userId);
            if (userEntity == null) {
                throw new BusinessException("指定用户不存在");
            }

            // 判断是否重复
            LambdaQueryWrapper<OrganizationUser> qw = new LambdaQueryWrapper<>();
            qw.eq(OrganizationUser::getOrganizationId, user.getOrganizationId())
                    .eq(OrganizationUser::getUserId, userId)
                    .eq(OrganizationUser::getRoleId, user.getRoleId());
            List<OrganizationUser> userList = organizationUserService.list(qw);
            if (userList.size() > 0) {
                throw new BusinessException("岗位配置信息重复");
            }

            // 添加岗位信息
            OrganizationUser orgUser = new OrganizationUser();
            orgUser.setOrganizationId(user.getOrganizationId());
            orgUser.setUserId(userEntity.getId());
            orgUser.setRoleId(user.getRoleId());
            orgUser.setIsPrimary(isPrimary);

            return organizationUserService.save(orgUser);
        }
        throw new BusinessException("传递参数错误");
    }

    /**
     * 重置用户密码
     *
     * @param userId
     * @return
     */
    @Override
    public boolean resetUserPassword(Long userId) {
        if (null == userId) {
            throw new BusinessException("用户不存在");
        }
        User oldInfo = this.getById(userId);
        if (oldInfo == null) {
            throw new BusinessException("用户不存在");
        }
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String cryptPwd = passwordEncoder.encode(AuthConstant.BCRYPT + oldInfo.getAccount() + DigestUtils.md5DigestAsHex(defaultPwd.getBytes()));
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getPassword, cryptPwd).eq(User::getId, userId);
        boolean result = this.update(updateWrapper);
        return result;
    }

    /**
     * 修改用户状态
     *
     * @param userId
     * @return
     */
    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        if (null == userId || null == status) {
            throw new BusinessException("参数错误");
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getStatus, status).eq(User::getId, userId);
        boolean result = this.update(updateWrapper);
        return result;
    }

    /**
     * 单个删除用户
     *
     * @param userId
     * @return
     */
    @Override
    public boolean deleteUser(Long userId) {
        boolean result = removeById(userId);
        if (result) {
            this.deleteUserRelation(userId);
        }
        return result;
    }

    /**
     * 批量删除用户
     *
     * @param userIds
     * @return
     */
    @Override
    public boolean batchDeleteUser(List<Long> userIds) {
        boolean result = removeByIds(userIds);
        if (result) {
            for (Long userId : userIds) {
                this.deleteUserRelation(userId);
            }
        }
        return result;
    }

    @Override
    public boolean updateAccount(@Valid UpdateAccountDTO account) {
        User userEntity = BeanCopierUtils.copyByClass(account, User.class);
        // 查询已存在的用户，用户名、昵称、邮箱、手机号有任一重复即视为用户已存在，真实姓名是可以重复的。
        List<User> userList = userMapper.queryExistUser(userEntity);
        if (!CollectionUtils.isEmpty(userList)) {
            throw new BusinessException("已存在的用户，用户名、昵称、邮箱、手机号有任一重复即视为用户已存在");
        }
        // 处理前端传过来的省市区
        userEntity = resolveAreas(userEntity, account.getAreas());
        return this.updateById(userEntity);
    }

    /**
     * 通过账号查询用户
     *
     * @param userAccount 用户账号
     * @return
     */
    @Override
    public User queryUserByAccount(String userAccount) {
        LambdaQueryWrapper<User> ew = new LambdaQueryWrapper<>();
        ew.and(e -> e.eq(User::getAccount, userAccount).or().eq(User::getEmail, userAccount).or().eq(User::getMobile,
                userAccount));
        return getOne(ew);
    }

    /**
     * 用户是否存在
     *
     * @param user
     * @return
     */
    @Override
    public Boolean checkUserExist(User user) {
        List<User> userList = userMapper.queryExistUser(user);
        // 如果存在则返回true
        if (!CollectionUtils.isEmpty(userList)) {
            return true;
        }
        return false;
    }

    /**
     * 登录时查询用户
     *
     * @param user
     * @return
     */
    @Override
    public UserInfo queryUserInfo(User user) {

        UserInfo userInfo = userMapper.queryUserInfo(user);

        if (null == userInfo) {
            throw new BusinessException(ResultCodeEnum.INVALID_USERNAME.getMsg());
        }

        // 取得用户组信息
        List<OrganizationUser> ouList = userMapper.queryUserRoleInfo(userInfo.getId());
        List<String> roleIds = new ArrayList<>();
        List<String> roleKeys = new ArrayList<>();
        for(OrganizationUser u: ouList) {
            roleIds.add(u.getRoleId().toString());
            roleKeys.add(u.getRoleKey());

            int isPrimary = u.getIsPrimary();
            if (isPrimary > 0) {
                // 取得主要岗位机构信息
                userInfo.setOrganizationId(u.getOrganizationId());
                userInfo.setOrganizationName(u.getOrganizationName());
            }
        }

        userInfo.setRoleIdList(roleIds);
        userInfo.setRoleKeyList(roleKeys);

//        String roleIds = userInfo.getRoleIds();
//        //组装角色ID列表，用于Oatuh2和Gateway鉴权
//        if (!StringUtils.isEmpty(roleIds)) {
//            String[] roleIdsArray = roleIds.split(StrUtil.COMMA);
//            userInfo.setRoleIdList(Arrays.asList(roleIdsArray));
//        }

//        String roleKeys = userInfo.getRoleKeys();
//        //组装角色key列表，用于前端页面鉴权
//        if (!StringUtils.isEmpty(roleKeys)) {
//            String[] roleKeysArray = roleKeys.split(StrUtil.COMMA);
//            userInfo.setRoleKeyList(Arrays.asList(roleKeysArray));
//        }
//
//        String dataPermissionTypes = userInfo.getDataPermissionTypes();
//        // 获取用户的角色数据权限级别
//        if (!StringUtils.isEmpty(dataPermissionTypes)) {
//            String[] dataPermissionTypeArray = dataPermissionTypes.split(StrUtil.COMMA);
//            userInfo.setDataPermissionTypeList(Arrays.asList(dataPermissionTypeArray));
//        }
//
//        String organizationIds = userInfo.getOrganizationIds();
//        // 获取用户机构数据权限列表
//        if (!StringUtils.isEmpty(organizationIds)) {
//            String[] organizationIdArray = organizationIds.split(StrUtil.COMMA);
//            userInfo.setOrganizationIdList(Arrays.asList(organizationIdArray));
//        }

        QueryUserResourceDTO queryUserResourceDTO = new QueryUserResourceDTO();
        queryUserResourceDTO.setUserId(userInfo.getId());
        List<Resource> resourceList = resourceService.queryResourceListByUserId(queryUserResourceDTO);

        // 查询用户权限列表key，用于前端页面鉴权
        List<String> menuList = resourceList.stream().map(Resource::getResourceKey).collect(Collectors.toList());
        userInfo.setResourceKeyList(menuList);

        // 查询用户资源列表，用于SpringSecurity鉴权
        List<String> resourceUrlList = resourceList.stream().filter(s -> !ResourceEnum.MODULE.getCode().equals(s.getResourceType()) && !ResourceEnum.MENU.getCode().equals(s.getResourceType())).map(Resource::getResourceUrl).collect(Collectors.toList());
        userInfo.setResourceUrlList(resourceUrlList);

        // 查询用户菜单树，用于页面展示
        List<Resource> menuTree = resourceService.queryMenuTreeByUserId(userInfo.getId());
        userInfo.setMenuTree(menuTree);

        return userInfo;
    }

    /**
     * 删除用户的关联关系
     *
     * @param userId
     */
    private void deleteUserRelation(Long userId) {
        if (null != userId) {
            //删除角色关联
            LambdaQueryWrapper<UserRole> wpd = new LambdaQueryWrapper<>();
            wpd.eq(UserRole::getUserId, userId);
            userRoleService.remove(wpd);
            //删除组织关联
            LambdaQueryWrapper<OrganizationUser> organizationUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            organizationUserLambdaQueryWrapper.eq(OrganizationUser::getUserId, userId);
            organizationUserService.remove(organizationUserLambdaQueryWrapper);
            //删除数据权限关联
            LambdaQueryWrapper<DataPermissionUser> dataPermissionUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dataPermissionUserLambdaQueryWrapper.eq(DataPermissionUser::getUserId, userId);
            dataPermissionUserService.remove(dataPermissionUserLambdaQueryWrapper);
        }
    }

    /**
     * 处理省市区数据
     *
     * @param userEntity
     * @param areas
     * @return
     */
    private User resolveAreas(User userEntity, List<String> areas) {
        if (!CollectionUtils.isEmpty(areas)) {
            userEntity.setProvince(areas.get(GitEggConstant.Address.PROVINCE));
            userEntity.setCity(areas.get(GitEggConstant.Address.CITY));
            userEntity.setArea(areas.size() > GitEggConstant.Number.TWO ? areas.get(GitEggConstant.Address.AREA) : StrUtil.SPACE);
        }
        return userEntity;
    }

}
