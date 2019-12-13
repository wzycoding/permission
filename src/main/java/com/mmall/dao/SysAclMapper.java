package com.mmall.dao;

import com.mmall.beans.PageQuery;
import com.mmall.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    /**
     * 保证同一权限模块下，权限点名称不重复
     * @param aclModuleId
     * @param name
     * @param id
     * @return
     */
    int countByName(
            @Param("aclModuleId") int aclModuleId,
            @Param("name")String name,
            @Param("id")Integer id);

    int countByAclModuleId(@Param("aclModuleId") int aclModuleId);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId") int aclModuleId,
                                      @Param("page")PageQuery page);


    List<SysAcl> getAll();

    List<SysAcl> getByIdList(@Param("idList")List<Integer> idList);

    List<SysAcl> getByUrl(@Param("url")String url);
}