package com.yunmuq.kingyanplus.config.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.dto.Permission;
import com.yunmuq.kingyanplus.dto.Role;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录后，sa-token会根据这里给用户配置权限
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    private final UserMapper userMapper;

    public StpInterfaceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<String> getPermissionList(Object o, String s) {
        // 每个需要认证的请求都会调用此方法，因此不能在此去查数据库
        return (List<String>) StpUtil.getSession().get("permissions");
    }

    /**
     * 没有代码层面的角色，只有数据库层面的角色，他是一些权限的集合
     * 代码上只需注解权限即可
     */
    @Override
    public List<String> getRoleList(Object o, String s) {
        //return userMapper.selectUserByUserName((String) o).getRoles();
        return null;
    }
}
