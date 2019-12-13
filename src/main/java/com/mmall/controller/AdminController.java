package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录成功之后的Controller
 * @author wzy
 * @version 1.0
 * @date 2019/12/4 8:55
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 登录成功之后跳转
     * @return
     */
    @RequestMapping("index.page")
    public ModelAndView index() {
        return new ModelAndView("admin");
    }
}
