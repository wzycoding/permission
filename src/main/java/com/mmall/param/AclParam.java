package com.mmall.param;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 权限点参数对象
 * @author wzy
 * @version 1.0
 * @date 2019/12/6 8:50
 */
@Data
@ToString
public class AclParam {
    private Integer id;

    @NotBlank(message = "权限点名称不可以为空")
    @Length(min = 2, max = 64, message = "权限点名称长度需要在2-64个字")
    private String name;

    @NotNull(message = "必须指定权限模块")
    private Integer aclModuleId;

    @Length(min = 6, max = 100, message = "权限点URL长度需要在6-256个字之间")
    private String url;

    @NotNull(message = "必须指定权限点的类型")
    @Min(value = 1, message = "权限点类型不合法")
    @Max(value = 3, message = "权限点类型不合法")
    private Integer type;


    @NotNull(message = "必须指定权限点的状态")
    @Min(value = 0, message = "权限点状态不合法")
    @Max(value = 1, message = "权限点状态不合法")
    private Integer status;

    @NotNull(message = "必须指定权限点的展示顺序")
    private Integer seq;

    @Length(min = 0, max = 200, message = "权限点备注长度需要在200个字符以内")
    private String remark;
}
