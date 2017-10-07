package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

@Controller
public class CartController {

	@Autowired
	private ItemService itemService;
	@Value("${CART_COOKIE_MAXAGE}")
	private int CART_COOKIE_MAXAGE;
	
	@Autowired
	private CartService cartService;
	
	/**
	 * 单个商品添加到购物车
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		// 判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			// 用户已登录，添加之redis中
			cartService.addCartItem(user.getId(), itemId, num);
			return "cartSuccess";
		}
		
		
		// 用户未登录，购物车添加至浏览器cookie中
		List<TbItem> list = getCartFromCookie(request);
		boolean flag = false;
		for (TbItem tbItem : list) {
			if (tbItem.getId() == itemId.longValue()) {
				flag = true;
				// 商品存在，增加商品的数量
				tbItem.setNum(num);
				break;
			}
		}
		if (!flag) {
			// 商品不存在，根据id查询指定的商品，然后将商品商量设置为num，并添加到集合中
			TbItem tbItem = itemService.getItemById(itemId);
			tbItem.setNum(num);
			String image = tbItem.getImage();
			if (StringUtils.isNoneBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			
			list.add(tbItem);
		}
		String json = JsonUtils.objectToJson(list);
		CookieUtils.setCookie(request, response, "cart", json, CART_COOKIE_MAXAGE, true);
		return "cartSuccess";
	}
	
	/**
	 * 展示购物车列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response) {
		List<TbItem> list = getCartFromCookie(request);
		// 已经登录
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			// 获取购物车，将cookie中的购物车与redis中的合并
			cartService.mergeCart(user.getId(), list);
			// 删除cookie中的购物车
			CookieUtils.deleteCookie(request, response, "cart");
			// 从redis中获取指定用户的购物车
			list = cartService.getCartByIdFromRedis(user.getId());
		}
		
		// 未登录的情况下
		request.setAttribute("cartList", list);
		return "cart";
	}
	
	/**
	 * 更新购物车的商品数量
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			E3Result result = cartService.updateCartNum(user.getId(), itemId, num);
			return result;
		}
		List<TbItem> list = getCartFromCookie(request);
		for (TbItem tbItem : list) {
			if (tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(num);
				break;
			}
		}
		// 将商品写会cookie;
		String json = JsonUtils.objectToJson(list);
		CookieUtils.setCookie(request, response, "cart", json, CART_COOKIE_MAXAGE, true);
		return E3Result.ok();
	}
	
	
	/**
	 * 根据商品id删除购物车中的商品
	 * @param itemId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteItemById(@PathVariable Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			cartService.deleteItemById(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		
		
		List<TbItem> list = getCartFromCookie(request);
		for (TbItem tbItem : list) {
			if (tbItem.getId() == itemId.longValue()) {
				list.remove(tbItem);
				break;
			}
		}
		// 将删除后的商品写入购物车
		String json = JsonUtils.objectToJson(list);
		CookieUtils.setCookie(request, response, "cart", json, CART_COOKIE_MAXAGE, true);
		return "redirect:/cart/cart.html";
	}
	
	/**
	 * 获取购物车商品的通用方法
	 * @param request
	 * @return
	 */
	public List<TbItem> getCartFromCookie(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isBlank(json)) {
			return new ArrayList<TbItem>();
		}
		List<TbItem> itemList = JsonUtils.jsonToList(json, TbItem.class);
		return itemList;
	}
	
}
