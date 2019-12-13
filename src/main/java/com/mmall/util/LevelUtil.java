package com.mmall.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 层级工具类
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 20:23
 */
public class LevelUtil {
    public static final String SEPARATOR = ".";

    public static final String ROOT= "0";
    //0
    //0.1
    //0.1.2
    //0.1.3
    //0.4
    public static String calculateLevel(String parentLevel, int parentId) {
        if (StringUtils.isBlank(parentLevel)) {
            return ROOT;
        } else {
            return StringUtils.join(parentLevel, SEPARATOR, parentId);
        }

    }
}
