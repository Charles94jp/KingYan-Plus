package com.yunmuq.kingyanplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 弃用的方案：dto对象，给前端的以及给sa-token去赋权的，应该是{@link com.yunmuq.kingyanplus.model.UserDetails}
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-21
 * @since 1.8
 * @since spring boot 2.6.6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private BigInteger id;
    private String name;
    private String password;
    /**
     * 角色是抽象的，它开源包含多个权限，并能在运行中更改。代码中只基于权限做校验
     */
    private List<Role> roles;
    /**
     * 用户真正的权限=用户的权限+用户角色中的权限，去重
     */
    private List<Permission> permissions;
    private String nickname;
    /**
     * 数据库中为char，0未知，1男，2女
     */
    private int sex;
    private String phone;
    private String email;
    private Date createdDate;
}
