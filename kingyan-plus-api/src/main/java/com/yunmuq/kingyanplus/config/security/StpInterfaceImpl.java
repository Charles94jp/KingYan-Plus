package com.yunmuq.kingyanplus.config.security;

import cn.dev33.satoken.stp.StpInterface;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
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
        return userMapper.selectUserByUserName((String) o).getRoles();
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return userMapper.selectUserByUserName((String) o).getRoles();
    }
}
