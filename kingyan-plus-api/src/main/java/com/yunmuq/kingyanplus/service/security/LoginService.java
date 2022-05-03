package com.yunmuq.kingyanplus.service.security;

import com.yunmuq.kingyanplus.dto.User;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
public interface LoginService {
    public User loadUserByUsername(String userName);
    public boolean matchUserPassword(User user,String pwd);
}
