package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${USER_INFO}")
	private String USER_INFO;
	@Value("${EXPIRE_TIME}")
	private Integer EXPIRE_TIME;

	@Override
	public E3Result checkRegister(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if (type == 1) {
			// 验证用户名
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			// 验证手机号
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			// 验证邮箱
			criteria.andEmailEqualTo(param);
		} else {
			// 非法参数
			return E3Result.build(400, "非法参数！");
		}
		
		List<TbUser> userList = userMapper.selectByExample(example);
		if (userList != null && userList.size() > 0) {
			// 不可以注册，返回false
			return E3Result.ok(false);
		}
		// 可以注册，返回true
		return E3Result.ok(true);
	}

	@Override
	public E3Result register(TbUser user) {
		// 后台检验数据合法性
		if (StringUtils.isBlank(user.getUsername())) {
			return E3Result.build(400, "用户名不能为空");
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return E3Result.build(400, "密码不能为空");
		}
		
		E3Result result = checkRegister(user.getUsername(), 1);
		if (!(boolean) result.getData()) {
			// 用户名存在，不可使用
			return E3Result.build(400, "用户已存在，请重新输入");
		}
		
		result = checkRegister(user.getPhone(), 2);
		if (!(boolean) result.getData()) {
			return E3Result.build(400, "电话号码存在，请重新输入");
		}
		
		user.setCreated(new Date());
		user.setUpdated(new Date());
		String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(password);
		userMapper.insert(user);
		return E3Result.ok();
	}

	@Override
	public E3Result login(String username, String password) {
		// 判断用户名密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		password = DigestUtils.md5DigestAsHex(password.getBytes());
		criteria.andPasswordEqualTo(password);
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return E3Result.build(400, "用户名或者密码错误");
		}
		// 用户名存在
		TbUser user = list.get(0);
		// 生成token，并返回
		String token = UUID.randomUUID().toString();
		// 把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		// 使用String类型保存Session信息。可以使用“前缀:token”为key
		user.setPassword(null);
		jedisClient.set(USER_INFO + ":" + token, JsonUtils.objectToJson(user));
		// 设置过期时间
		jedisClient.expire(USER_INFO + ":" + token, EXPIRE_TIME);
		return E3Result.ok(token);
	}

	@Override
	public E3Result getUserByToken(String token) {
		// 根据token从redis中查询数据
		String tokenJson = jedisClient.get(USER_INFO + ":" + token);
		if(StringUtils.isBlank(tokenJson)){
				// 不存在，返回用户登录已过期
			return E3Result.build(222, "用户登录已经过期");
		}
		TbUser user = JsonUtils.jsonToPojo(tokenJson, TbUser.class);
		// 存在，则获取用户信息，并刷新token
		jedisClient.expire(USER_INFO + ":" + token, EXPIRE_TIME);
		return E3Result.ok(user);
	}

}
