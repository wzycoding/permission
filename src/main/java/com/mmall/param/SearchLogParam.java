package com.mmall.param;

import lombok.Data;
import lombok.ToString;

/**
 * 查询日志入参
 * @author wzy
 * @version 1.0
 * @date 2019/12/15 15:14
 */
@Data
@ToString
public class SearchLogParam {
    private Integer type;

    private String beforeSeq;

    private String afterSeq;

    private String operator;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String fromTime;

    private String toTime;
}
