package com.yunmuq.kingyanplus.mapper;

import com.yunmuq.kingyanplus.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    void selectUserByUserName() {
        User admin = userMapper.selectUserByUserName("admin");
        System.out.println(admin);
        admin = userMapper.selectUserByUserNameWithoutAuth("admin");
        System.out.println(admin);
    }
}