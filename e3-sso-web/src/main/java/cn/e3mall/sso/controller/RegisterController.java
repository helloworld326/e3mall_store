package cn.e3mall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

@Controller
public class RegisterController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody // 不可忘
	public E3Result checkRegister(@PathVariable String param, @PathVariable Integer type) {
		E3Result result = userService.checkRegister(param, type);
		return result;
	}
	
	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public E3Result registerUser(TbUser user) {
		E3Result result = userService.register(user);
		return result;
	}
	
}
