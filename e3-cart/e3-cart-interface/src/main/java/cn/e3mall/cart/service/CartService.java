package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {

	E3Result addCartItem(Long userId, Long itemId, Integer num);

	E3Result mergeCart(Long id, List<TbItem> list);

	List<TbItem> getCartByIdFromRedis(Long id);

	E3Result updateCartNum(Long id, Long itemId, Integer num);

	void deleteItemById(Long userId,Long itemId);
	
	E3Result deleteCartAllByUid(Long userId);

}
