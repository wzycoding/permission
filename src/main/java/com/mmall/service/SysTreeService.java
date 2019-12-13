package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclDto;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptLevelDto;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 转树形结构服务
 * @author wzy
 * @version 1.0
 * @date 2019/12/1 20:55
 */
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    /**
     * 获取指定用户的权限信息，返回也是以一颗树的形式返回
     * @param userId
     * @return
     */
    public List<AclModuleLevelDto> userAclTree(int userId) {
        //获取指定用户所拥有的权限点
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);

        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl sysAcl : userAclList) {
            AclDto dto = AclDto.adapt(sysAcl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId) {
        //1、当前用户分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        //2、角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        //3、当前系统所有的权限点
        List<AclDto> aclDtoList = Lists.newArrayList();


        //当前用户已分配的权限id set
        Set<Integer> userAclIdSet = userAclList.stream()
                .map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        //当前角色的权限id set
        Set<Integer> roleAclSet = roleAclList.stream()
                .map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();

        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if (roleAclSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }

        return aclListToTree(aclDtoList);
    }

    /**
     * 根据权限点信息生成树
     * @param aclDtoList
     * @return
     */
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }

        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();

        for (AclDto acl : aclDtoList) {
            if (acl.getStatus() == 1) {
                moduleIdAclMap.put(acl.getAclModuleId(), acl);

            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    /**
     * 将权限点绑定到权限模块树上
     */
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList,
                                  Multimap<Integer, AclDto> moduleIdAclMap) {
        if(CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }

        for (AclModuleLevelDto dto: aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>) moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }

    /**
     * 返回部门树
     * @return
     */
    public List<DeptLevelDto> deptTree() {
        //取出所有的部门信息
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            //做属性拷贝和转换
            DeptLevelDto dto = DeptLevelDto.addDept(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleTree() {
        //取出所有权限模块的信息
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();

        List<AclModuleLevelDto> aclModuleLevelDtoList = Lists.newArrayList();

        for (SysAclModule aclModule : aclModuleList) {
            //拷贝属性
            AclModuleLevelDto dto = AclModuleLevelDto.adapt(aclModule);
            aclModuleLevelDtoList.add(dto);
        }
        return aclModuleListToTree(aclModuleLevelDtoList);

    }

    private List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> aclModuleLevelDtoList) {
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)) {
            return Lists.newArrayList();
        }
        //level ->[AclModule, AclModule, ...] 数据准备
        Multimap<String, AclModuleLevelDto> aclModuleLevelMap = ArrayListMultimap.create();
        //一级部门树形结构的根
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for (AclModuleLevelDto dto : aclModuleLevelDtoList) {
            aclModuleLevelMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }

        //按照seq排序
        Collections.sort(rootList, aclModuleSeqComparator);

        //递归生成树
        transformAclModuleTree(rootList, LevelUtil.ROOT, aclModuleLevelMap);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> aclModuleLevelDtoList, String level, Multimap<String, AclModuleLevelDto> aclModuleLevelMap) {
        for (AclModuleLevelDto dto : aclModuleLevelDtoList) {

            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());

            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) aclModuleLevelMap.get(nextLevel);

            //说明还有下一层
            if (CollectionUtils.isNotEmpty(tempList)) {
                //排序
                Collections.sort(tempList, aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, aclModuleLevelMap);
            }
        }
    }

    /**
     * 其实就是把每一个dept下面的节点都列出来
     * @param deptLevelList
     * @return
     */
    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if (CollectionUtils.isEmpty(deptLevelList)) {
            return Lists.newArrayList();
        }
        //level ->[dept1, dept2, ...],value相同层级的dept,这里要记住这个包是google的
        //Map<String, List<Object>
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        //一级部门，树形结构的根
        List<DeptLevelDto> rootList = Lists.newArrayList();

        for (DeptLevelDto dto : deptLevelList) {
            //相同level，保存到相同的key下面
            levelDeptMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        //按照seq排序
        Collections.sort(rootList, deptSeqComparator);

        //递归生成树
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);
        return rootList;
    }

    /**
     * 递归排序, level:0 ,0 ,all 0 ->0.1, 0.2
     *
     * level:0.1
     * level:0.2
     * @param deptLevelList
     * @param level
     * @param levelDeptMap
     */
    private void transformDeptTree(List<DeptLevelDto> deptLevelList, String level, Multimap<String, DeptLevelDto> levelDeptMap) {
        for (int i = 0; i < deptLevelList.size(); i++) {
            //遍历该层的每一个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            //处理当前的层级,最开始是root：0，计算下一个层级的level
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            //处理下一层,获取到下一层的部门列表
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            //如果还有子部门
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                //排序
                Collections.sort(tempDeptList, deptSeqComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层进行处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }

    /**
     * 比较器对象
     */
    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    private Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    private Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

}
