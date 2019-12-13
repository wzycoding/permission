package com.mmall.util;

import java.util.Random;

/**
 * 密码生成工具类
 * @author wzy
 * @version 1.0
 * @date 2019/12/3 21:52
 */
public class PasswordUtil {
    public final static String[] WORD = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    public final static String[] NUM = {
            "2", "3", "4", "5", "6", "7", "8", "9"
    };
    public static String randomPassword() {
        StringBuffer stringBuffer = new StringBuffer();
        //相同时间戳返回的结果就是一样的
        Random random = new Random(System.currentTimeMillis());
        //通过flag来表示我们应该选取数字还是字母
        boolean flag = false;
        //密码8 - 10位
        int length = random.nextInt(3) + 8;
        for (int i = 0; i < length; i++) {
            if (flag) {
                stringBuffer.append(NUM[random.nextInt(NUM.length)]);
            }
            if (!flag) {
                stringBuffer.append(WORD[random.nextInt(WORD.length)]);
            }
            flag = !flag;
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) throws Exception{
        System.out.println(randomPassword());
    }
}
