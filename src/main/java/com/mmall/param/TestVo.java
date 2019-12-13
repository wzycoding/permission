package com.mmall.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 0:33
 */
@Data
public class TestVo {

    @NotBlank
    private String msg;

    //不为null
    @NotNull
    //是否大于某个值,或最小值
    @Max(29)
    //自定义message
    @Min(value = 0, message = "id 不能小于0")
    private Integer id;

    //size=0
    @NotEmpty
    private List<String> str;

}
