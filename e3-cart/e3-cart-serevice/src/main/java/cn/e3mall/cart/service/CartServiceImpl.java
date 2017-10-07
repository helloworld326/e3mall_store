
package cn.e3mall.cart.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	@Autowired
	private JedisClient jedisClient;
	@Value("${CART_REDIS_PRE}")
	private String CART_REDIS_PRE;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public E3Result addCartItem(Long userId, Long itemId, Integer num) {
		// 判断商品是否存在
		Boolean isExists = jedisClient.hexists(CART_REDIS_PRE + itemId, itemId + "");
		if (isExists) {
			// 存在，数量相加
			String json = jedisClient.hget(CART_REDIS_PRE + itemId, itemId + "");
			TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
			item.setNum(item.getNum() + num);
			// 添加至购物车
			jedisClient.hset(CART_REDIS_PRE + userId, itemId + "", JsonUtils.objectToJson(item));
			return E3Result.ok();
		}
		// 不存在，添加至redis中
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		item.setNum(num);
		// 对图片进行处理
		String images = item.getImage();
		if (StringUtils.isNotBlank(images)) {
			String image = images.split(",")[0];
			item.setImage(image);
		}
		jedisClient.hset(CART_REDIS_PRE + userId, itemId + "", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	public E3Result mergeCart(Long id, List<TbItem> list) {
		for (TbItem tbItem : list) {
			addCartItem(id, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}

	public List<TbItem> getCartByIdFromRedis(Long id) {
		List<String> list = jedisClient.hvals(CART_REDIS_PRE + id);
		List<TbItem> itemList = new ArrayList<>();
		for (String itemStr : list) {
			TbItem item = JsonUtils.jsonToPojo(itemStr, TbItem.class);
			itemList.add(item);
		}
		return itemList;
	}

	@Override
	public E3Result updateCartNum(Long id, Long itemId, Integer num) {
		String json = jedisClient.hget(CART_REDIS_PRE + id, itemId + "");
		TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
		item.setNum(num);
		// 写入redis中
		jedisClient.hset(CART_REDIS_PRE + id, itemId + "", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	@Override
	public void deleteItemById(Long userId, Long itemId) {
		jedisClient.hdel(CART_REDIS_PRE + userId, itemId + "");
	}

	@Override
	public E3Result deleteCartAllByUid(Long userId) {
		jedisClient.del(CART_REDIS_PRE + userId);
		return E3Result.ok();
	}

}
