package com.mmall.beans;

import lombok.*;

import javax.validation.constraints.Min;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/4 13:24
 */
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {
    @Getter
    @Setter
    @Min(value =1, message = "当前页码不合法")
    private int pageNo = 1;

    @Getter
    @Setter
    @Min(value = 1, message = "每页展示数量不合法")
    private int pageSize = 10;

    /**
     * 偏移量
     */
    @Setter
    private int offset;

    public int getOffset() {
        return (pageNo - 1) * pageSize;
    }
}
