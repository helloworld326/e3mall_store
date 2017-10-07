package cn.e3mall.cart.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;
	
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从cookie中获取token信息
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		if (StringUtils.isBlank(token)) {
			// 判断是否存在，否则直接放行
			return true;
		}
		// 用户已经登陆，调用sso服务，根据token获取用户信息
		E3Result result = userService.getUserByToken(token);
		if (result.getStatus() == 222) {
			// token过期直接放行
			return true;
		}
		TbUser user = (TbUser) result.getData();
		// 获取用户信息并将该信息存放如request中
		request.setAttribute("user", user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
