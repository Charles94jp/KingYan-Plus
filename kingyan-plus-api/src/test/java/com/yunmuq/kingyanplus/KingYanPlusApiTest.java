package com.yunmuq.kingyanplus;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-04
 * @since 1.8
 * @since spring boot 2.6.7
 */
public class KingYanPlusApiTest {
    @Test
    public void Test(){
        Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9]{3,}$");
        Matcher matcher = userNamePattern.matcher("123");
        Matcher matcher1 = userNamePattern.matcher("1234-");
        System.out.println();
    }
}
