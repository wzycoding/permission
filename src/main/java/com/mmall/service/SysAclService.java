package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAcl;
import com.mmall.param.AclParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 权限点Service
 * @author wzy
 * @version 1.0
 * @date 2019/12/6 9:15
 */
@Service
@Slf4j
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;

    public void save(AclParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getAclModuleId(), param.getName(), param.getId())) {
            throw new ParamException("同一权限模块下的权限点的名称不能重复");
        }
        SysAcl sysAcl = SysAcl.builder()
                .name(param.getName())
                .aclModuleId(param.getAclModuleId())
                .seq(param.getSeq())
                .status(param.getStatus())
                .type(param.getType())
                .url(param.getUrl())
                .remark(param.getRemark()).build();

        sysAcl.setCode(generateCode());

        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAcl.setOperatorTime(new Date());
        sysAclMapper.insertSelective(sysAcl);
    }

    public void update(AclParam aclParam) {
        BeanValidator.check(aclParam);
        if (checkExist(aclParam.getAclModuleId(), aclParam.getName(), aclParam.getId())) {
            throw new ParamException("同一权限模块下存在相同的权限点名称");
        }

        SysAcl before = sysAclMapper.selectByPrimaryKey(aclParam.getId());
        Preconditions.checkNotNull(before);

        SysAcl after = SysAcl.builder()
                .aclModuleId(aclParam.getAclModuleId())
                .name(aclParam.getName())
                .remark(aclParam.getRemark())
                .seq(aclParam.getSeq())
                .status(aclParam.getStatus())
                .type(aclParam.getType())
                .url(aclParam.getUrl())
                .id(aclParam.getId())
                .build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorTime(new Date());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.updateByPrimaryKeySelective(after);
    }

    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);

        //计算总数
        int count = sysAclMapper.countByAclModuleId(aclModuleId);

        if (count > 0) {
            List<SysAcl> pageResult = sysAclMapper.getPageByAclModuleId(aclModuleId, pageQuery);
            return PageResult.<SysAcl>builder().total(count).data(pageResult).build();
        }
        return PageResult.<SysAcl>builder().build();
    }

    private boolean checkExist(int aclModuleId, String name, Integer id) {
        return sysAclMapper.countByName(aclModuleId, name, id) > 0;
    }

    /**
     * 生成code
     * @return
     */
    private String generateCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()) + "_" + (int)(Math.random() * 100);
    }
}
