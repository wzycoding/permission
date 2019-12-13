package com.mmall.param;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 部门参数类
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 19:56
 */
@Data
public class DeptParam {
    private Integer id;

    @NotBlank(message = "部门名称不可以为空")
    @Length(max = 15, min = 2, message = "部门名称长度要在2-15之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "展示顺序不可以为空")
    private Integer seq;

    @Length(max = 150, message = "备注长度不能超过150个字")
    private String remark;


}
