package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

// MessageListener的依赖是activemq-all

@SuppressWarnings("all")
public class HtmlGenListener implements MessageListener {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Autowired
	private ItemService itemService;
	@Value("${STATIC_PAGE_PATH}")
	private String STATIC_PAGE_GEN; // 静态页面存放路径
	
	@Override
	public void onMessage(Message message) {
		try {
			// 创建一个模板
			// 从消息中获取商品id
			TextMessage text = (TextMessage) message;
			String idStr = text.getText();
			Long itemId = new Long(idStr);
			// 等待事务提交
			Thread.sleep(1000);
			// 根据商品id查询商品信息，包括商品基本信息和商品描述 
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			TbItemDesc itemDesc = itemService.getItemDescById(itemId);
			System.out.println(itemId);
			System.out.println(itemDesc);
			// 创建一个数据集，封装商品数据
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", itemDesc);
			// 加载模板对象
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			// 创建输出流，指定输出目录
			Writer out = new FileWriter(new File(STATIC_PAGE_GEN + itemId + ".html"));
			// 生成静态页面
			template.process(data, out);
			// 关闭资源
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
