package com.jn.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//需要权限描述
@Target({ ElementType.TYPE, ElementType.METHOD })
/*
 *@Target说明了Annotation所修饰的对象范围：Annotation可被用于 packages、types
 * （类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、
 * 方法参数和本地变量（如循环变量、catch参数）。
 * 在Annotation类型的声明中使用了target可更加明晰其修饰的目标。
 * 作用：用于描述注解的使用范围（即：被描述的注解可以用在什么地方）
 * */
@Retention(RetentionPolicy.RUNTIME)
/*
* @Retention作用是定义被它所注解的注解保留多久，一共有三种策略，定义在RetentionPolicy枚举中.
从注释上看：
source：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；被编译器忽略
class：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期
runtime：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
这3个生命周期分别对应于：Java源文件(.java文件) ---> .class文件 ---> 内存中的字节码。
* */

public @interface RequiresPermissionsDesc {
	String[] menu();
	String button();
}
