package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 20:49
 */
@Data
@ToString
public class DeptLevelDto extends SysDept{
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    /**
     * 仅做了属性拷贝，相当于做了从model->Dto
     * @param dept
     * @return
     */
    public static DeptLevelDto addDept(SysDept dept) {
        DeptLevelDto dto = new DeptLevelDto();
        //属性相互拷贝
        BeanUtils.copyProperties(dept, dto);
        return dto;
    }

}
