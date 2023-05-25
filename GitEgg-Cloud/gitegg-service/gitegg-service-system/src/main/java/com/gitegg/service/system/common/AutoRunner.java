package com.gitegg.service.system.common;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gitegg.platform.base.constant.RedisConstant;
import com.gitegg.platform.base.permission.ActionPO;
import com.gitegg.platform.base.permission.ControllerPO;
import com.gitegg.platform.base.util.MyObjectUtil;
import com.gitegg.platform.base.util.MyStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统初始化时自动执行的操作
 */
@Component
public class AutoRunner implements CommandLineRunner {
    final static String SYS_CODE = "SYSTEM";

    @Autowired
    private PermissionManager manager;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("<<<<<<<<<<<<< 加载授权数据 >>>>>>>>>>>>>>>");
        manager.init();

        System.out.println("=========== 将控制器信息发送至 Nacos 配置管理器 ===========");
        updateControllerList();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private void updateControllerList() {
        String json = MyObjectUtil.getString(redisTemplate.opsForValue().get(RedisConstant.PERMISSION_KEY));
        JSONObject controllerJson;
        if (MyStringUtils.isNoneEmpty(json)) {
            controllerJson = JSONUtil.parseObj(json);
        } else {
            controllerJson = new JSONObject();
        }

        for (Map.Entry<String, ControllerPO> en : manager.getControllers().entrySet()) {
            // String k = en.getKey();
            System.out.println("更新控制器：" + en.getKey());

            ControllerPO v = en.getValue();
            Map<String, Object> m = new HashMap<>();
            m.put("id", v.getId());
            m.put("name", v.getName());
            m.put("code", v.getCode());
            m.put("auth", v.getAuth());
            m.put("path", v.getPath());
            m.put("sys", SYS_CODE);

            HashMap<String, ActionPO> pos = v.getActionPOs();
            HashMap<String, Map> actions = new HashMap<>();
            for (Map.Entry<String, ActionPO> en1 : pos.entrySet()) {
                HashMap<String, String> act = new HashMap<>();
                ActionPO po = en1.getValue();
                act.put("id", po.getId());
                act.put("name", po.getName());
                act.put("code", po.getCode());
                act.put("auth", po.getAuth());
                act.put("path", po.getPath());
                actions.put(en1.getKey(), act);
            }
            m.put("actions", actions);

            controllerJson.set(en.getKey(), m);
        }

        redisTemplate.opsForValue().set(RedisConstant.PERMISSION_KEY, controllerJson.toString());
        MyStringUtils.printJson(controllerJson);
        System.out.println("=========== 保存完毕 ===========");
    }

}