package com.mmall.controller;

import com.mmall.common.ApplicationContextHelper;
import com.mmall.common.JsonData;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.param.TestVo;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * SpringMVC测试Controller
 * @author wzy
 * @version 1.0
 * @date 2019/11/29 21:33
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {
    @ResponseBody
    @RequestMapping("/hello.json")
    public JsonData hello() {
        log.info("hello");
        throw new RuntimeException("自定义异常");
//        return JsonData.success("hello, permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        log.info("validate");
//        try {
//            Map<String, String> map = BeanValidator.validateObject(vo);
//            if (MapUtils.isNotEmpty(map)) {
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    log.info("{}->{}", entry.getKey(), entry.getValue());
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        BeanValidator.check(vo);

        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule sysAclModule = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(sysAclModule));
        return JsonData.success("test validate");
    }


}
