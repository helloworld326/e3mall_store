package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;

public interface UserService {

	/**
	 * 异步检验用户注册信息功能
	 * @param param
	 * @param type
	 * @return
	 */
	E3Result checkRegister(String param, int type);
	
	/**
	 * 注册功能
	 * @return
	 */
	E3Result register(TbUser user);
	
	/**
	 * 用户单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	E3Result login(String username, String password);
	
	/**
	 * 根据token信息获取用户
	 * @param token
	 * @return
	 */
	E3Result getUserByToken(String token);
	
}
