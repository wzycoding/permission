package com.mmall.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysRole;
import com.mmall.model.SysUser;
import com.mmall.param.RoleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 * @author wzy
 * @version 1.0
 * @date 2019/12/7 23:53
 */
@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLogService sysLogService;

    public void save(RoleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getName(), param.getId())) {
            throw new ParamException("不能有同名的角色信息");
        }
        SysRole sysRole = SysRole.builder()
                .id(param.getId())
                .name(param.getName())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .type(param.getType())
                .status(param.getStatus())
                .build();
        sysRole.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRole.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysRole.setOperatorTime(new Date());
        sysRoleMapper.insertSelective(sysRole);

        //更新日志
        sysLogService.saveRoleLog(null, sysRole);
    }

    private boolean checkExist(String name, Integer id) {
        return sysRoleMapper.countByName(name, id) > 0;
    }


    public void update(RoleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getName(), param.getId())) {
            throw new ParamException("不能存在同名的角色");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的角色不存在");

        SysRole after = SysRole.builder()
                .id(param.getId())
                .name(param.getName())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .type(param.getType())
                .status(param.getStatus())
                .build();
        after.setOperatorTime(new Date());
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysRoleMapper.updateByPrimaryKeySelective(after);

        //更新日志
        sysLogService.saveRoleLog(before, after);
    }

    public List<SysRole> getAll() {
        return sysRoleMapper.getAll();
    }

    public List<SysRole> getRoleListByUserId(int userId) {
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }

    /**
     * 根据权限点id获取，角色列表
     * @param aclId
     * @return
     */
    public List<SysRole> getRoleListByAclId(int aclId) {
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }

    public List<SysUser> getUserListByRoleList(List<SysRole> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return Lists.newArrayList();
        }

        List<Integer> roleIdList = roleList.stream().map(sysRole -> sysRole.getId()).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);

        if (CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }

        return sysUserMapper.getByIdList(userIdList);

    }


}
