package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.model.SysAcl;
import com.mmall.model.SysUser;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限管理核心服务
 * @author wzy
 * @version 1.0
 * @date 2019/12/8 22:32
 */
@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysCacheService sysCacheService;

    /**
     * 获取当前用户的权限列表
     * @return
     */
    public List<SysAcl> getCurrentUserAclList() {
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }


    /**
     * 某一个角色已经分配的权限点
     * @param roleId
     * @return
     */
    public List<SysAcl> getRoleAclList(int roleId) {
        List<Integer> aclIdList =
                sysRoleAclMapper.getAclListByRoleIdList(Lists.newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)) {
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    /**
     * 根据用户id获取用户权限列表：已分配的所有权限
     * @param userId
     * @return
     */
    public List<SysAcl> getUserAclList(int userId) {
        //判断是否是超级管理员
        if (isSuperAdmin()) {
            return sysAclMapper.getAll();
        }

        //用户已经分配的角色id
        List<Integer> userRoleIdList =
                sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleIdList)) {
            return Lists.newArrayList();
        }

        List<Integer> userAclIdList = sysRoleAclMapper.getAclListByRoleIdList(userRoleIdList);

        if (CollectionUtils.isEmpty(userAclIdList)) {
            return Lists.newArrayList();
        }

        return sysAclMapper.getByIdList(userAclIdList);
    }

    /**
     * 是否是超级用户
     * @return
     */
    public boolean isSuperAdmin() {
        SysUser currentUser = RequestHolder.getCurrentUser();
        String adminPhoneNo = "17681826219";
        if (currentUser.getTelephone().equals(adminPhoneNo)) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否可以访问url
     * @param url
     * @return
     */
    public boolean hasUrlAcl(String url) {
        if (isSuperAdmin()) {
            return true;
        }

        List<SysAcl> aclList = sysAclMapper.getByUrl(url);
        //重点，如果为空的话，说明不关心这个权限点（url）允许访问
        if (CollectionUtils.isEmpty(aclList)) {
            return true;
        }
        //规则：只要有一个权限点有权限，就认为是有权限的

        // /sys/user/action.json，【对这一步进行缓存】
        List<SysAcl> userAclList = getCurrentUserAclListFromCache();
        //用户的权限点id集合
        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        //是否有有效的权限点
        boolean hasValidAcl = false;
        for (SysAcl sysAcl : aclList) {
            //判断一个用户是否具有某个权限点的访问权限
            if (sysAcl == null || sysAcl.getStatus() != 1) {
                //权限点无效
                continue;
            }
            hasValidAcl = true;
            if (userAclIdSet.contains(sysAcl.getId())) {
                return true;
            }
        }
        //这种情况是为了处理所有的权限都是被冻结的
        if (!hasValidAcl) {
            return true;
        }
        return false;
    }

    /**
     * 从缓存中获取用户的所有权限点
     * @return
     */
    public List<SysAcl> getCurrentUserAclListFromCache() {
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue =
                sysCacheService.getFromCache(CacheKeyConstants.USER_ACLS, String.valueOf(userId));
        if (StringUtils.isBlank(cacheValue)) {
            List<SysAcl> aclList = getCurrentUserAclList();
            if (CollectionUtils.isNotEmpty(aclList)) {
                //过期时间为十分钟
                sysCacheService.saveCache(JsonMapper.obj2String(aclList), 600, CacheKeyConstants.USER_ACLS, String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Object(cacheValue, new TypeReference<List<SysAcl>>(){});
    }
}
