package com.jn.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * 订单验证接口
 * */
@Target({METHOD, FIELD, PARAMETER})
/*
 * 它也是元注解（修饰注解的注解），用于描述注解的适用场景（即注解的使用地方），
 * 通过定义ElementType的取值类型实现，其类型取值如下：
 * */
@Retention(RUNTIME)
/*
 * 定义被它所注解的注解保留多久，一共有三种策略，定义在RetentionPolicy枚举中.从注释上看：
 * source：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；被编译器忽略
 * class：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期
 * runtime：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
 * */
@Documented
/*
 * 这个注解只是用来标注生成javadoc的时候是否会被记录。
 * */
@Constraint(validatedBy = OrderValidator.class)
/*
 * 自定义验证
 * */
public @interface Order {
    String message() default "排序类型不支持";

    String[] accepts() default {"desc", "asc"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
