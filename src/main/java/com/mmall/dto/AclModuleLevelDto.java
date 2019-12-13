package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysAclModule;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/5 13:24
 */
@Data
@ToString
public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

    private List<AclDto> aclList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule sysAclModule) {
        AclModuleLevelDto aclModuleDto = new AclModuleLevelDto();
        BeanUtils.copyProperties(sysAclModule, aclModuleDto);
        return aclModuleDto;
    }
}
