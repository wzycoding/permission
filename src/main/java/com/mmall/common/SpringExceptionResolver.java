package com.mmall.common;

import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局请求拦截
 * @author wzy
 * @version 1.0
 * @date 2019/11/29 23:12
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        String url = request.getRequestURL().toString();
        ModelAndView mv;
        String defaultMsg = "System error";

        //要求.json, .page来区分页面请求和json请求
        if (url.endsWith(".json")) {
            if (ex instanceof PermissionException  || ex instanceof ParamException) {
                JsonData result = JsonData.fail(ex.getMessage());
                //toMap就是为了保证正常和失败的返回结果都相同
                mv = new ModelAndView("jsonView", result.toMap());
            } else {
                log.error("unknow json exception, url:" + url, ex);
                JsonData result = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView", result.toMap());
            }
        } else if(url.endsWith(".page")) {
            log.error("unknow page exception, url:" + url, ex);
            JsonData result = JsonData.fail(defaultMsg);
            //去寻找exception.jsp
            mv = new ModelAndView("exception", result.toMap());
        } else {
            log.error("unknow  exception, url:" + url, ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception", result.toMap());
        }
        return mv;
    }
}
