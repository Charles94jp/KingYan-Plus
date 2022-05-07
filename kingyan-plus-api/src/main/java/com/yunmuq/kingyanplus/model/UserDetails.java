package com.yunmuq.kingyanplus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 弃用的方案：除了权限、角色和密码，其余的都应该和{@link com.yunmuq.kingyanplus.dto.User}相同
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-07
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    private BigInteger id;
    private String name;
    /**
     * 用户真正的权限=用户的权限+用户角色中的权限，去重
     * String对象，只有权限名，方便传给sa-token
     */
    private List<String> permissions;
    private String nickname;
    /**
     * 数据库中为char，0未知，1男，2女
     */
    private int sex;
    private String phone;
    private String email;
    private Date createdDate;
}
