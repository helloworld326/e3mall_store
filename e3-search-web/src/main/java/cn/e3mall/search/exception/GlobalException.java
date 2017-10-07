package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalException implements HandlerExceptionResolver {
	
	// Logger org.slf4j.Logger;
	// LoggerFactory org.slf4j.LoggerFactory;
	Logger logger = LoggerFactory.getLogger(GlobalException.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		
		// 写日志文件
		logger.error("系统发生异常", ex);
		// 发送邮件，短信
		// 展示错误页面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/exception");
		return modelAndView;
	}

}
