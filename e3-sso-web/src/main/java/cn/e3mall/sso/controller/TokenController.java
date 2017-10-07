package cn.e3mall.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.sso.service.UserService;

/**
 * 用户sso token Controller
 * @author 轻舞飞扬
 * @version
 * @date2017年9月21日
 * 
 */
@Controller
public class TokenController {
	
	@Autowired
	private UserService userService;
	
	// 方法一
//	@RequestMapping(value = "/user/token/{token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	@ResponseBody
//	public String getUserByToken(@PathVariable String token, String callback) {
//		E3Result result = userService.getUserByToken(token);
//		if (StringUtils.isNotBlank(callback)) {
//			String resultStr = callback + "(" + JsonUtils.objectToJson(result) + ");";
//			return resultStr;
//		}
//		return JsonUtils.objectToJson(result);
//	}
	
	
	// 方法二
	/**
	 * jsonp跨域访问
	 * @param token
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/user/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		E3Result result = userService.getUserByToken(token);
		if (StringUtils.isNotBlank(callback)) {
			// 把结果封装成一个js语句响应
			MappingJacksonValue jacksonValue = new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}
		return result;
	}
	
	

}
