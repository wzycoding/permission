package com.mmall.dao;

import com.mmall.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(@Param("id") Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    /**
     * 返回部门列表
     * @return
     */
    List<SysDept> getAllDept();

    /**
     * 获取子部门通过level
     * @param level
     * @return
     */
    List<SysDept> getChildDeptListByLevel(@Param("level") String level);


    void batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList);

    int countByNameAndParentId(
            @Param("parentId") Integer parentId,
            @Param("name") String name,
            @Param("id") Integer id
    );

    //TODO
    int countByParentId (@Param("parentId") int deptId);

}