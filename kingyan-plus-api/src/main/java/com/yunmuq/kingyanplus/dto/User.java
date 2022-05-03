package com.yunmuq.kingyanplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 不要将密码返回给前端
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
    private List<String> roles;
    private String nickname;
    /**
     * 数据库中为char，0未知，1男，2女
     */
    private int sex;
    private String phone;
    private String email;
    private Date createdDate;
}
