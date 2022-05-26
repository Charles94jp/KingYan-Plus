package com.yunmuq.kingyanplus.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-04
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private boolean success;
    private String message;
}
