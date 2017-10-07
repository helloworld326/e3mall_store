package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

/**
 * 商品搜索Controller
 * <p>Title: SearchController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */

@Controller
public class SearchController {
	
	@Autowired
	private SearchService searchService;
	
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;

	@RequestMapping("/search")
	public String searchIndex(Model model, 
			@RequestParam(defaultValue = "1") Integer page, String keyword) throws Exception {
		// 对keyword进行转码
		keyword = new String(keyword.getBytes("iso-8859-1"), "UTF-8");
		SearchResult result = searchService.search(keyword, page, SEARCH_RESULT_ROWS);
		model.addAttribute("totalPages", result.getTotalPages());
		model.addAttribute("query", keyword);
		model.addAttribute("itemList", result.getItemList());
		model.addAttribute("page", page);
		model.addAttribute("recourdCount", result.getRecordCount());
		return "search";
	}
	
}
