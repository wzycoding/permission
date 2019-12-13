package com.mmall.dto;

import com.mmall.model.SysAcl;
import lombok.Data;
import lombok.ToString;
import org.codehaus.jackson.map.util.BeanUtil;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author wzy
 * @version 1.0
 * @date 2019/12/8 22:04
 */
@Data
@ToString
public class AclDto extends SysAcl {
    /**
     * 是否被选中,默认选中
     */
    private boolean checked = false;

    /**
     * 是否有权限操作：能分配的权限要小于自己的权限
     */
    private boolean hasAcl = false;

    public static AclDto adapt(SysAcl sysAcl) {
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(sysAcl, dto);
        return dto;
    }
}
