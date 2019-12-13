package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.ws.Holder;
import java.util.Date;
import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 20:14
 */
@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    public void save(DeptParam param) {
        //参数校验
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的部门");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        //得到上一个部门的层级,当前层级="父层级level" + "父部门的id"，
        // 比如： 父层级0.1 和 父id为2 最终当前层级就为"0.1.2"
        dept.setLevel(LevelUtil.calculateLevel
                (getLevel(param.getParentId()), param.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());



        //insertSelective 不是全量插入，insert方法全量插入
        sysDeptMapper.insertSelective(dept);
    }

    /**
     * 校验是否重名
     * @param parentId
     * @param deptName
     * @return
     */
    private boolean checkExist(Integer parentId, String deptName, Integer id) {
        return sysDeptMapper.countByNameAndParentId(parentId, deptName, id) > 0;

    }

    public String getLevel(Integer deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (dept == null ) {
            return null;
        }
        return dept.getLevel();

    }


    public void update(DeptParam param) {
        //参数校验
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的部门");
        }

        //判断当前更新的部门是否存在
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());

        Preconditions.checkNotNull(before, "待更新的部门不存在");
        SysDept after = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).id(param.getId()).build();

        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before, after);
    }

//    @Transactional

    /**
     * 更新部门信息，以及子部门的level信息
     * @param before
     * @param after
     */
    private void updateWithChild(SysDept before, SysDept after) {
        //判断是否需要更新子部门
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if (!after.getLevel().equals(before.getLevel())) {
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(deptList)) {
                for (SysDept dept : deptList) {
                    String level = dept.getLevel();
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }

        sysDeptMapper.updateByPrimaryKeySelective(after);
    }

    public void deleteByDeptId(int deptId) {
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(sysDept, "待删除的部门不存在无法删除");
        if (sysDeptMapper.countByParentId(sysDept.getId()) > 0) {
            throw new ParamException("当前部门下面存在子部门，无法删除");
        }
        if (sysUserMapper.countByDeptId(deptId) > 0) {
            throw new ParamException("当前部门下面存在用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}
