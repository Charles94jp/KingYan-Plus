package com.yunmuq.kingyanplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-21
 * @since 1.8
 * @since spring boot 2.6.6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private int id;
    private String name;
    private List<Permission> permissions;
}
