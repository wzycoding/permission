package com.mmall.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 对SearchLogParam 进行转换，变成数据库里查询的参数
 * @author wzy
 * @version 1.0
 * @date 2019/12/15 15:18
 */
@Data
@ToString
public class SearchLogDto {
    /**
     * 参考LogType中的值
     */
    private Integer type;

    private String beforeSeq;

    private String afterSeq;

    private String operator;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private Date fromTime;

    private Date toTime;
}
