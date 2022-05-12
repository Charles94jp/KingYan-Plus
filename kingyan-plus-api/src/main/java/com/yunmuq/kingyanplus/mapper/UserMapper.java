package com.yunmuq.kingyanplus.mapper;

import com.yunmuq.kingyanplus.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserByUserName(String userName);
    User selectUserByUserNameWithoutAuth(String userName);
    int insertUser(User user);
}
