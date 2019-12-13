package com.mmall.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.JsonData;
import com.mmall.model.SysRole;
import com.mmall.model.SysUser;
import com.mmall.param.RoleParam;
import com.mmall.service.*;
import com.mmall.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色Controller
 * @author wzy
 * @version 1.0
 * @date 2019/12/7 23:13
 */
@RequestMapping("/sys/role")
@Controller
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysTreeService sysTreeService;

    @Resource
    private SysRoleAclService sysRoleAclService;

    @Resource
    private SysRoleUserService sysRoleUserService;

    @Resource
    private SysUserService sysUserService;

    @ResponseBody
    @RequestMapping("/role.page")
    public ModelAndView page() {
        ModelAndView mv = new ModelAndView("role");
        return mv;
    }

    @ResponseBody
    @RequestMapping("/save.json")
    public JsonData save(RoleParam param) {
        sysRoleService.save(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/update.json")
    public JsonData update(RoleParam param) {
        sysRoleService.update(param);
        return JsonData.success();
    }

    @ResponseBody
    @RequestMapping("/list.json")
    public JsonData list() {

        List<SysRole> sysRoleList = sysRoleService.getAll();
        return JsonData.success(sysRoleList);
    }


    @ResponseBody
    @RequestMapping("roleTree.json")
    public JsonData roleTree(@RequestParam("roleId") int roleId) {
        return JsonData.success(sysTreeService.roleTree(roleId));
    }


    @ResponseBody
    @RequestMapping("/changeAcls.json")
    public JsonData changeAcls(@RequestParam("roleId") int roleId,
                               @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds) {
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);

        sysRoleAclService.changeRoleAcls(roleId, aclIdList);
        return JsonData.success();
    }


    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") int roleId) {
        List<SysUser> selectUserList = sysRoleUserService.getListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();

        List<SysUser> unSelectedUserList = Lists.newArrayList();
        Set<Integer> selectIdUserIdSet =
                selectUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
        for (SysUser sysUser : allUserList) {
            if (sysUser.getStatus() == 1 && !selectIdUserIdSet.contains(sysUser.getId())) {
                unSelectedUserList.add(sysUser);
            }
        }

        //可以去过滤状态不等于1的用户
//        selectUserList = selectUserList.stream().filter(sysUser -> sysUser.getStatus() != 1).collect(Collectors.toList());
        Map<String, List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectUserList);
        map.put("unselected", unSelectedUserList);
        return JsonData.success(map);
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam("roleId") int roleId,
                                @RequestParam(value = "userIds", required = false, defaultValue = "")String userIds) {
        List<Integer> userIdList = StringUtil.splitToListInt(userIds);
        sysRoleUserService.changeRoleUsers(roleId, userIdList);
        return JsonData.success();
    }
}
