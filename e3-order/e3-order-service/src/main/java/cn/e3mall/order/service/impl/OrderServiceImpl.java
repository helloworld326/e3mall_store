package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ORDER_GEN_ID}")
	private String ORDER_GEN_ID;
	@Value("${ORDER_GEN_START_ID}")
	private String ORDER_GEN_START_ID;
	@Value("${ORDER_DETAIL_GEN_ID}")
	private String ORDER_DETAIL_GEN_ID;

	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		// 生成订单号，使用redis的incr
		if (!jedisClient.exists(ORDER_GEN_ID)) {
			// 订单号不存在，先设置起始订单号
			jedisClient.set(ORDER_GEN_ID, ORDER_GEN_START_ID);
		}
		String orderId = jedisClient.incr(ORDER_GEN_ID).toString();
		// 补全OrderInfo属性
		orderInfo.setOrderId(orderId);
		// 付款状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		// 插入订单表
		orderMapper.insert(orderInfo);
		// 插入订单明细表
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			String orderItemsId = jedisClient.incr(ORDER_DETAIL_GEN_ID).toString();
			tbOrderItem.setId(orderItemsId);
			tbOrderItem.setOrderId(orderId);
			orderItemMapper.insert(tbOrderItem);
		}
		// 插入物流表
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		// 返回e3result
		return E3Result.ok(orderId);
	}

}
