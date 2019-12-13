package com.mmall.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * 参数校验工具validator
 * @author wzy
 * @version 1.0
 * @date 2019/11/30 11:24
 */
public class BeanValidator {
    /**
     * validator工厂
     */
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    /**
     * 验证一个对象是否符合规则
     * @param t
     * @param groups
     * @param <T>
     * @return
     */
    public static <T>Map<String, String> validate(T t, Class... groups) {
        //获取validator
        Validator validator = validatorFactory.getValidator();
        //获取校验结果
        Set validateResult = validator.validate(t, groups);
        //如果是Empty说明没错误，直接返回空的Map
        if (validateResult.isEmpty()) {
            return Collections.emptyMap();
        } else {
            //返回错误结果
            LinkedHashMap errors = Maps.newLinkedHashMap();
            Iterator iterator = validateResult.iterator();
            //循环校验结果，每一个值都是校验结果
            while (iterator.hasNext()) {
                //校验结果
                ConstraintViolation violation = (ConstraintViolation) iterator.next();
                //将ConstraintViolation里面的错误放到map当中<有问题字段，错误信息>
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    /**
     * 验证集合元素
     * @param collection
     * @return
     */
    public static Map<String, String> validateList(Collection<?> collection) {
        //谷歌工具，判断集合是否为空，空直接抛出异常
        Preconditions.checkNotNull(collection);
        Iterator iterator = collection.iterator();

        Map errors;

        //循环做参数校验
        do {
            if (!iterator.hasNext()) {
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object, new Class[0]);
        }while (errors.isEmpty());

        return errors;

    }

    public static Map<String, String > validateObject(Object first, Object... objects) {
        if (objects != null && objects.length > 0) {
            return validateList(Lists.asList(first, objects));
        } else {
            return validate(first, new Class[0]);
        }
    }

    public static void check(Object  param) throws ParamException {
        Map<String, String> map = BeanValidator.validateObject(param);
        if (MapUtils.isNotEmpty(map)) {
            throw new ParamException(map.toString());
        }
    }
}
