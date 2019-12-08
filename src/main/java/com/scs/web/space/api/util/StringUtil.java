package com.scs.web.space.api.util;

import java.util.Random;

/**
 * @ClassName StringUtil
 * @Description TODO
 * @Author mq_xu
 * @Date 2019/12/2
 **/
public class StringUtil {
    /**
     * 获取六位随机数短信验证码
     *
     * @return
     */
    public static String getVerifyCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(String.valueOf(random.nextInt(10)));
        }
        return stringBuilder.toString();
    }
}
