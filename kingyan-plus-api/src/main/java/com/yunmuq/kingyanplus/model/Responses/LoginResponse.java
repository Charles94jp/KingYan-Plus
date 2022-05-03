package com.yunmuq.kingyanplus.model.Responses;

import com.yunmuq.kingyanplus.dto.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-02
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String msg;
    private User user;
}
