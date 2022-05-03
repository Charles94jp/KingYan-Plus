package com.yunmuq.kingyanplus.service.security;

import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Service
public class LoginServiceImpl implements LoginService{
    @Autowired
    UserMapper userMapper;

    /**
     *
     * @param userName
     * @return 不要把密码返回前端
     */
    @Override
    public User loadUserByUsername(String userName) {
        return userMapper.selectUserByUserName(userName);
    }


    @Override
    public boolean matchUserPassword(User user, String pwd) {

        return false;
    }


}
