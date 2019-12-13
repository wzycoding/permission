package com.mmall.beans;

import lombok.*;

import java.util.Set;

/**
 * EMail信息
 * @author wzy
 * @version 1.0
 * @date 2019/12/5 0:07
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    /**
     * 主题
     */
    private String subject;

    /**
     * 邮件信息
     */
    private String message;

    /**
     * 收件人
     */
    private Set<String> receivers;
}
