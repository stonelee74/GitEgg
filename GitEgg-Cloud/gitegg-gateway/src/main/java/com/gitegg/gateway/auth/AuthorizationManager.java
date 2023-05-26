package com.gitegg.gateway.auth;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gitegg.platform.base.constant.AuthConstant;
import com.gitegg.platform.base.constant.RedisConstant;
import com.gitegg.platform.base.constant.TokenConstant;
import com.gitegg.platform.base.permission.ActionPO;
import com.gitegg.platform.base.permission.ControllerPO;
import com.gitegg.platform.base.util.MyObjectUtil;
import com.gitegg.platform.oauth2.props.AuthUrlWhiteListProperties;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.*;

/**
 * 网关鉴权管理器
 * @author GitEgg
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    /**
     * 路由对应的控制器代码和所需授权
     */
    class CA {
        String controller;
        String auth;
    }

    private final RedisTemplate redisTemplate;

    /**
     * oauth-list 全局配置
     */
    private final AuthUrlWhiteListProperties authUrlWhiteListProperties;

    /**
     * 完全匹配的路由权限
     */
    private static HashMap<String, CA> routeMap = new HashMap<>();

    /**
     * 按前缀匹配的路由权限
     */
    private static HashMap<String, CA> prefixMap = new HashMap<>();

    /**
     * 是否开启租户模式
     */
    @Value("${tenant.enable}")
    private Boolean enable;


    synchronized private void reloadPeimissionData() {
        // 重新加载
        log.info("重新加载授权数据");
        String json = MyObjectUtil.getString(redisTemplate.opsForValue().get(RedisConstant.PERMISSION_KEY));
        if (!StringUtils.isEmpty(json)) {
            JSONObject cMap = JSONUtil.parseObj(json);
            // 遍历所有控制器
            for (Map.Entry<String, Object> entry: cMap.entrySet()) {
                Object obj = entry.getValue();
                if (obj instanceof JSONObject) {
                    JSONObject po = (JSONObject)obj;
                    String path = po.getStr("path");

                    // 遍历控制器所有方法
                    JSONObject actions = po.getJSONObject("actions");
                    for (Map.Entry<String, Object> entry1: actions.entrySet()) {
                        Object obj1  = entry1.getValue();
                        if (obj1 instanceof JSONObject) {
                            JSONObject apo = (JSONObject)obj1;
                            // 记录 URL 对应控制器及所需授权
                            String path1 = path + apo.getStr("path");
                            CA ca = new CA();
                            ca.controller = po.getStr("code");
                            ca.auth = apo.getStr("auth");
                            if (apo.getBool("prefix", false)) {
                                routeMap.put(path1, ca);
                            } else {
                                prefixMap.put(path1, ca);
                            }
                            log.info("{} 对应控制权限：{} -- {}", path1, ca.controller, ca.auth);
                        }
                    }
                }
            }
        }

        redisTemplate.opsForValue().set(RedisConstant.PERMISSION_RELOAD_KEY, false);
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();

        // 取得是否重新加载权限数据标志
        boolean reload = MyObjectUtil.getBoolean(redisTemplate.opsForValue().get(RedisConstant.PERMISSION_RELOAD_KEY));
        if (reload || routeMap.size() < 1) {
            reloadPeimissionData();
        }

        // 取得资源路径
        String path = request.getURI().getPath();

        // 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // token为空拒绝访问
        String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }

        // Basic认证直接放行,此处需注意：请不要将所有带Basic头的直接放行，否则可以直接绕过网关认证，从而访问其他微服务
        if (token.startsWith(AuthConstant.JWT_TOKEN_PREFIX_BASIC)
                && !CollectionUtils.isEmpty(authUrlWhiteListProperties.getTokenUrls())
                && authUrlWhiteListProperties.getTokenUrls().contains(path))
        {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 如果token被加入到黑名单，就是执行了退出登录操作，那么拒绝访问
        String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
        try {
            JWSObject jwsObject = JWSObject.parse(realToken);
            Payload payload = jwsObject.getPayload();
            JSONObject jsonObject = JSONUtil.parseObj(payload.toString());
            String jti = jsonObject.getStr(TokenConstant.JTI);
            String blackListToken = (String)redisTemplate.opsForValue().get(AuthConstant.TOKEN_BLACKLIST + jti);
            if (!StringUtils.isEmpty(blackListToken)) {
                return Mono.just(new AuthorizationDecision(false));
            }
        } catch (ParseException e) {
            log.error("获取token黑名单时发生错误：{}", e);
        }

        // 如果开启了租户模式，但是请求头里没有租户信息，那么拒绝访问
        String tenantId = request.getHeaders().getFirst(AuthConstant.TENANT_ID);
        if (enable && StringUtils.isEmpty(tenantId)) {
            return Mono.just(new AuthorizationDecision(false));
        }

        // 需要鉴权但是每一个角色都需要的url，统一配置，不需要单个配置
        List<String> authUrls = authUrlWhiteListProperties.getAuthUrls();
        PathMatcher pathMatcher = new AntPathMatcher();
        String urls = authUrls.stream().filter(url -> pathMatcher.match(url, path)).findAny().orElse(null);

        // 当配置了功能鉴权url时，直接放行，用户都有的功能，但是必须要登录才能用，例：退出登录功能是每个用户都有的权限，但是这个必须要登录才能够调用
        if (null != urls) {
            return mono.filter(Authentication::isAuthenticated)
                    .map(auth -> new AuthorizationDecision(true))
                    .defaultIfEmpty(new AuthorizationDecision(false));
        }

        String redisRoleKey = AuthConstant.TENANT_RESOURCE_ROLES_KEY;
        // 判断是否开启了租户模式，如果开启了，那么按租户分类的方式获取角色权限
        if (enable) {
            redisRoleKey += tenantId;
        } else {
            redisRoleKey = AuthConstant.RESOURCE_ROLES_KEY;
        }

        // 匹配访问路径
        CA ca = routeMap.get(path);
        if (ca == null) {
            for (Map.Entry<String, CA> entry : prefixMap.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    ca = entry.getValue();
                }
            };
        }

        if (ca != null) {
            log.info("访问：{} 需要权限 {} -- {}", path, ca.controller, ca.auth);
        }

        // 缓存取资源权限角色关系列表
        Map<Object, Object> resourceRolesMap = redisTemplate.opsForHash().entries(redisRoleKey);
        Iterator<Object> iterator = resourceRolesMap.keySet().iterator();

        // 请求路径匹配到的资源需要的角色权限集合authorities统计
        List<String> authorities = new ArrayList<>();
        while (iterator.hasNext()) {
            String pattern = (String) iterator.next();
            if (pathMatcher.match(pattern, path)) {
                authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
            }
        }

        Mono<AuthorizationDecision> authorizationDecisionMono = mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(roleKey -> {
                    // roleId是请求用户的角色(格式:ROLE_{roleKey})，authorities是请求资源所需要角色的集合
                    log.info("访问路径：{}", path);
                    log.info("用户角色roleKey：{}", roleKey);
                    log.info("资源需要权限authorities：{}", authorities);
                    return authorities.contains(roleKey);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
        return authorizationDecisionMono;

    }
}

