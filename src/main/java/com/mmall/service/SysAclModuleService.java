package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.param.AclModuleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 权限模块service
 * @author wzy
 * @version 1.0
 * @date 2019/12/5 8:58
 */
@Service
public class SysAclModuleService {

    @Resource
    SysAclModuleMapper sysAclModuleMapper;

    @Resource
    SysAclMapper sysAclMapper;

    public void save(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder()
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .status(param.getStatus())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()))
                .remark(param.getRemark()).build();
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateTime(new Date());

        //保存权限模块信息
        sysAclModuleMapper.insertSelective(aclModule);
    }

    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        //校验传入的id对应的实体类是否存在
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before);

        SysAclModule after = SysAclModule.builder()
                .name(param.getName())
                .id(param.getId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .parentId(param.getParentId())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()))
                .build();
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        updateWithChild(before, after);
    }

    private void updateWithChild(SysAclModule before, SysAclModule after) {
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();

        if (!after.getLevel().equals(before.getLevel())) {
            List<SysAclModule> aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(aclModuleList)) {
                for (SysAclModule aclModule : aclModuleList) {
                    String level = aclModule.getLevel();
                    //第一次出现的位置
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        //拼接出新的level：新的prefix + 后面的内容
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }

        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId, String aclModuleName, Integer id) {
        return sysAclModuleMapper.countByNameAndParentId(aclModuleName, parentId, id) > 0;
    }

    /**
     * 获取父层级的level
     * @param aclModuleId
     * @return
     */
    private String getLevel(Integer aclModuleId) {
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (sysAclModule == null) {
            return null;
        }
        return sysAclModule.getLevel();

    }

    public void deleteByAclModuleId(int id) {
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(aclModule, "待删除的权限模块不存在，无法删除");
        //查询是否存在子模块
        if (sysAclModuleMapper.countByParentId(aclModule.getId()) > 0) {
            throw new ParamException("当前权限模块存在子模块，无法删除");
        }
        //查询是否存在权限点
        if (sysAclMapper.countByAclModuleId(aclModule.getId()) > 0) {
            throw new ParamException("当前权限模块存在权限点，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(id);
    }
}
