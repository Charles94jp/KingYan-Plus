package com.yunmuq.kingyanplus.config.security;

import cn.dev33.satoken.stp.StpInterface;
import com.yunmuq.kingyanplus.dto.Permission;
import com.yunmuq.kingyanplus.dto.Role;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        User user = userMapper.selectUserByUserName((String) o);
        ArrayList<String> permissionList = new ArrayList<>();
        // 各roles中的权限有重复的。user自身的也有重复的，不去重也不会有问题
        for (Role r : user.getRoles()) {
            for (Permission p : r.getPermissions()) {
                permissionList.add(p.getName());
            }
        }
        for (Permission p : user.getPermissions()) {
            permissionList.add(p.getName());
        }
        return permissionList;
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
