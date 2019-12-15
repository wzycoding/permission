package com.mmall.controller;

import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.JsonData;
import com.mmall.model.SysLogWithBLOBs;
import com.mmall.param.SearchLogParam;
import com.mmall.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * 日志记录Controller
 * @author wzy
 * @version 1.0
 * @date 2019/12/14 10:07
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    private SysLogService sysLogService;

    @RequestMapping("/log.page")
    public ModelAndView page() {
        ModelAndView mv = new ModelAndView("log");
        return mv;
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData search(SearchLogParam param, PageQuery page) {
        PageResult<SysLogWithBLOBs> result= sysLogService.searchPageList(param, page);
        return JsonData.success(result);
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@RequestParam("id") int id) {
        sysLogService.recover(id);
        return JsonData.success();
    }





}
