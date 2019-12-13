package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.MD5Util;
import com.mmall.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户Service
 * @author wzy
 * @version 1.0
 * @date 2019/12/3 21:13
 */
@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    public void save(UserParam userParam) {
        BeanValidator.check(userParam);
        if (checkEmailExist(userParam.getMail(), userParam.getId())) {
            throw new ParamException("邮箱已经存在");
        }
        if (checkTelephone(userParam.getTelephone(), userParam.getId())) {
            throw new ParamException("电话已被占用");
        }

        String password = PasswordUtil.randomPassword();
        //TODO:因为这里不能发送邮件，所以暂时设置默认初始密码
        password = "12345678";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser sysUser = SysUser.builder()
                .deptId(userParam.getDeptId())
                .username(userParam.getUsername())
                .mail(userParam.getMail())
                .telephone(userParam.getTelephone())
                .password(encryptedPassword)
                .status(userParam.getStatus())
                .remark(userParam.getRemark())
                .build();

        //TODO:
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysUser.setOperatorTime(new Date());

        //TODO:发送email，之后才能创建用户
        sysUserMapper.insertSelective(sysUser);
    }

    public void update(UserParam param) {
        BeanValidator.check(param);
        if (checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已经存在");
        }
        if (checkTelephone(param.getTelephone(), param.getId())) {
            throw new ParamException("电话已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        //待更新的用户不能不存在
        Preconditions.checkNotNull(before, "待更新的用户不存在");

        SysUser after = SysUser.builder()
                .username(param.getUsername())
                .deptId(param.getDeptId())
                .mail(param.getMail())
                .remark(param.getRemark())
                .status(param.getStatus())
                .telephone(param.getTelephone())
                .id(param.getId())
                .build();
        after.setOperatorTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());

        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkTelephone(String telephone, Integer id) {
        return sysUserMapper.countByTelephone(telephone, id) > 0;
    }

    private boolean checkEmailExist(String email, Integer id) {

        return sysUserMapper.countByMail(email, id) > 0;
    }

    /**
     * 同时支持手机号登录和邮箱登录
     * @param keyword 关键字（手机号或者邮箱）
     * @return sysUser
     */
    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page) {
        BeanValidator.check(page);
        //TODO:
        //计算总数
        int count = sysUserMapper.countByDeptId(deptId);

        if (count > 0) {
            List<SysUser> userList = sysUserMapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().total(count).data(userList).build();
        }
        return PageResult.<SysUser>builder().build();
    }


    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }
}
