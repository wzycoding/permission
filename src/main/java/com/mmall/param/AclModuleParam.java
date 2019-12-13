package com.mmall.param;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 权限模块参数
 * @author wzy
 * @version 1.0
 * @date 2019/12/5 8:57
 */
@Data
@ToString
public class AclModuleParam {
    private Integer id;

    @NotBlank(message = "权限模块名称不能为空")
    @Length(min = 2, max = 20, message = "权限模块名称长度在2-20之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "权限模块展示顺序不可以为空")
    private Integer seq;

    @NotNull(message = "权限模块状态也不能为空")
    @Min(value = 0, message = "权限模块状态不合法")
    @Max(value = 1, message = "权限模块状态不合法")
    private Integer status;

    @Length(max = 64, message = "权限模块的备注需要在200个字以内")
    private String remark;


}
