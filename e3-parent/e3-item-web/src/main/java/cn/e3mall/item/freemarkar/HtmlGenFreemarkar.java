package cn.e3mall.item.freemarkar;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 生成静态页面controller
 * @author 轻舞飞扬
 * @version
 * @date2017年9月19日
 * 
 */
@SuppressWarnings("all")
@Controller
public class HtmlGenFreemarkar {
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/genhtml")
	@ResponseBody
	public String genHtml() throws Exception {
		// 1.从容器中获取FreemarkarConfigurer对象
		// 2.从FreemarkarConfigurer中获取Configuration对象
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		// 3.使用configuration对象获取template对象
		Template template = configuration.getTemplate("hello.ftl");
		// 4.创建数据集
		Map data = new HashMap<>();
		data.put("hello", "1000");
		// 5.创建输出文件的writer对象
		Writer out = new FileWriter("E:/hello2.html");
		// 使用模板对象template调用process方法
		template.process(data, out);
		out.close();
		return "ok";
	}
	
}
