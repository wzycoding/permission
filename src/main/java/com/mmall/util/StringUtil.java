package com.mmall.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/10 8:40
 */

public class StringUtil {

    public static List<Integer> splitToListInt(String str) {
        List<String> strList = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(str);

        return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
    }
}
