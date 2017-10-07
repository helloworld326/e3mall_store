package cn.e3mall.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerTest {
	
	public static void main(String[] args) throws Exception {
		// 创建一个模板文件
		// 创建一个configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		// 设置模板文件保存目录
		configuration.setDirectoryForTemplateLoading(new File("D:/BackUpCode/source/e3-parent/e3-item-web/src/main/webapp/WEB-INF/ftl/"));
		// 设置编码格式
		configuration.setDefaultEncoding("UTF-8");
		// 加载一个模板文件，创建一个模板对象
		Template template = configuration.getTemplate("hello.ftl");
		// 创建数据集，pojo or map , recommend map
		Map data = new HashMap<>();
		data.put("hello", "my first freemarker project");
		// 创建writer对象
		Writer out = new FileWriter(new File("e:/hello.txt"));
		// 生成静态文件
		template.process(data, out);
		// 闭流
		out.close();
	}
	
}
