package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	@Value("${USER_LOGIN_URL}")
	private String USER_LOGIN_URL;
	
	@Autowired
	private UserService userService;
	@Autowired
	private CartService cartService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从cookie中获取token信息
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		// 判断是否存在
		if (StringUtils.isBlank(token)) {
			// 不存在，跳转到sso登录页面并携带referer url
			response.sendRedirect(USER_LOGIN_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 如果存在，判断token是否过期，否则同上
		E3Result result = userService.getUserByToken(token);
		if (result.getStatus() == 222) {
			// 不存在，跳转到sso登录页面并携带referer url
			response.sendRedirect(USER_LOGIN_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 用户已经登录，获取用户信息
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		// 获取cookie中的购物车信息
		String json = CookieUtils.getCookieValue(request, "cart", true);
		// 判断购物车是否为空，否，则合并购物车
		if (StringUtils.isNoneBlank(json)) {
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
