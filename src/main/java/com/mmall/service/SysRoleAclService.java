package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.beans.LogType;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysLogMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.model.SysLogWithBLOBs;
import com.mmall.model.SysRoleAcl;
import com.mmall.util.IpUtil;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/10 8:45
 */
@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    public void  changeRoleAcls(int roleId, List<Integer> aclIdList) {
        //取出之前的数据
        List<Integer> originAclIdList = sysRoleAclMapper.getAclListByRoleIdList(Lists.newArrayList(roleId));

        if (originAclIdList.size() == aclIdList.size()) {
            Set<Integer> originAclSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);

            originAclSet.removeAll(aclIdSet);
            if (CollectionUtils.isEmpty(originAclSet)) {
                return;
            }
        }

        updateRoleAcls(roleId, aclIdList);
        saveRoleAclLog(roleId, originAclIdList, aclIdList);
    }

    @Transactional
    public void updateRoleAcls(int roleId, List<Integer> aclIdList) {
        sysRoleAclMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }

        List<SysRoleAcl> roleAclList = Lists.newArrayList();

        for (Integer aclId : aclIdList) {
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder()
                    .roleId(roleId)
                    .aclId(aclId)
                    .operator(RequestHolder.getCurrentUser().getUsername())
                    .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operatorTime(new Date())
                    .build();
            roleAclList.add(sysRoleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
