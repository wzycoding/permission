package com.mmall.beans;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/4 13:24
 */
@Getter
@Setter
@ToString
@Builder
public class PageResult<T> {
    private List<T> data = Lists.newArrayList();

    private int total = 0;
}
