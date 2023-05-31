package com.gitegg.service.system.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gitegg.platform.base.annotation.auth.WebPermission;
import com.gitegg.platform.base.constant.RedisConstant;
import com.gitegg.platform.base.exception.BusinessException;
import com.gitegg.platform.base.result.Result;
import com.gitegg.platform.base.util.MyObjectUtil;
import com.gitegg.platform.base.util.MyStringUtils;
import com.gitegg.service.system.dto.QueryUserDTO;
import com.gitegg.service.system.entity.User;
import com.gitegg.service.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "common")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "CommonController|通用控制器", tags = {"通用功能"})
@RefreshScope
@WebPermission(label = "通用控制器")
public class CommonController {

    private final IUserService userService;

    private final RedisTemplate redisTemplate;

    @GetMapping("/simpleList")
    @ApiOperation(value = "查询用户列表")
    @WebPermission(label = "查询用户列表")
    public Result<List<User>> simpleList(@ApiIgnore QueryUserDTO user) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.select(User::getId, User::getAccount, User::getNickname, User::getRealName);
        qw.orderByAsc(User::getRealName);
        return Result.data(userService.list(qw));
    }

    @GetMapping("/getController")
    @ApiOperation(value = "取得控制器列表")
    @WebPermission(label = "取得控制器列表")
    public Result<?> getController(@RequestParam(required = false) String module) {
        String json = MyObjectUtil.getString(redisTemplate.opsForValue().get(RedisConstant.PERMISSION_KEY));
        if (MyStringUtils.isNoneEmpty(json)) {
            JSONObject controllerJson = JSONUtil.parseObj(json);
            if (MyStringUtils.isEmpty(module)) {
                return Result.data(controllerJson.values());
            } else {
                ArrayList<JSONObject> list = new ArrayList<>();
                for (Object obj : controllerJson.values()) {
                    if (obj == null) continue;
                    JSONObject jo = (JSONObject) obj;
                    if (module.equals(jo.get("sys"))) {
                        list.add(jo);
                    }
                }
                return Result.data(list);
            }
        }

        throw new BusinessException("无法取得控制器信息");
    }
}
