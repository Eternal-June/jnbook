package com.jn.admin.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jn.core.util.ResponseUtil;

/*
* 为了方便对异常的统一管理，spring mvc提供了ControllerAdvice注解对异常进行统一的处理，
* 拿到这些异常信息后，可以做一些处理，比如提供一个统一的web界面查看异常信息，或者普通到异常信息后，
* 发送短信、邮件形式通知到相关人员，可以帮助开发人员快速发现并定位问题，
* 减少以往通过查看线上日志文件排查问繁琐锁耗时的所耗费的时间。
* */
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
/*
* @Order 注解
* @Order注解主要用来控制配置类的加载顺序 ：数字越小，越先加载
* */
public class ShiroExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	/*
	* Spring的@ExceptionHandler可以用来统一处理方法抛出的异常
	* 当我们使用这个@ExceptionHandler注解时，我们需要定义一个异常的处理方法
	* */
	@ResponseBody
	/*
	* 该注解是将方法的返回结果直接写入到HTTP的Response的Body中，一般用在异步加载信息使用
	* 通常与@RequestMapping("")连用
	* RequestMapping("")注解一般把返回值路径进行拦截（或跳转），
	* 加上ResponseBody后 就会把方法返回的内容加入到response的body 中
	* ,否则会抱404错误
	* */
	public Object unauthenticatedHandler(AuthenticationException e) {
		e.printStackTrace();
		return ResponseUtil.unlogin();
	}

	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public Object unauthorizedHandler(AuthorizationException e) {
		e.printStackTrace();
		return ResponseUtil.unauthz();
	}

}
