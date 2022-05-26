package com.yunmuq.kingyanplus.mapper;

import com.yunmuq.kingyanplus.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectUserByUserName(String userName);
    User selectUserByUserNameWithoutAuth(String userName);
    int insertUser(User user);
    int addUserPermission(@Param("userName") String userName, @Param("permissionName") String permissionName);

    int deleteUserPermission(@Param("userName") String userName, @Param("permissionName") String permissionName);
}
