package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	/**
	 * 页面跳转，并能将redirect保存到域中
	 * @param page
	 * @param redirect 即referer
	 * @param model
	 * @return
	 */
	@RequestMapping("/page/{page}")
	public String page(@PathVariable String page, String redirect, Model model) {
		model.addAttribute("redirect", redirect);
		return page;
	}
	
	/**
	 * 登录功能，登录成功后可跳转回跳转到登录页面的网页（前台实现，后台将原网页地址存入域中）
	 * @return
	 */
	@RequestMapping(value = "user/login", method = RequestMethod.POST)
	@ResponseBody
	public E3Result login(String username, String password, 
			HttpServletRequest request, HttpServletResponse response) {
		E3Result result = userService.login(username, password);
		if (result.getStatus() == 200) {
			// 用户登陆成功 
			String token = result.getData().toString();
			CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token);
		}
		return result;
	}
	
}
