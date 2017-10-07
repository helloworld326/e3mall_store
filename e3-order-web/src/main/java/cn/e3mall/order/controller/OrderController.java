package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

/**
 * 订单业务控制层
 * @author 轻舞飞扬
 * @version
 * @date2017年9月23日
 * 
 */
@Controller
public class OrderController {

	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	/**
	 * 订单页面展示
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		// 获取用户
		TbUser user = (TbUser) request.getAttribute("user");
		// 获取用户购物车列表
		List<TbItem> list = cartService.getCartByIdFromRedis(user.getId());
		request.setAttribute("cartList", list);
		return "order-cart";
	}
	
	/**
	 * 生成订单
	 * @param orderInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/order/create", method = RequestMethod.POST)
	public String crateOrder(OrderInfo orderInfo, HttpServletRequest request) {
		// 获取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		// 把用户信息添加到OrderInfo中
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		// 调用服务层生成订单
		E3Result result = orderService.createOrder(orderInfo);
		if (result.getStatus() == 200) {
			// 删除购物车中的信息
			cartService.deleteCartAllByUid(user.getId());
		}
		// 把订单号传递给页面
		request.setAttribute("orderId", result.getData());
//		request.setAttribute("orderId", orderInfo.getOrderId());
		request.setAttribute("payment", orderInfo.getPayment());
		return "success";
	}
	
}
