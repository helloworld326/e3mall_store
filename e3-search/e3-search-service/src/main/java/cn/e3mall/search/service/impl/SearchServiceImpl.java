package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;

/**
 * 商品搜索Service
 * <p>Title: SearchServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class SearchServiceImpl implements SearchService {
	
	@Value("${DEFAULT_FIELD}")
	private String DEFAULT_FIELD;
	
	@Autowired
	private SearchDao searchDao;

	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {
		
		SolrQuery query = new SolrQuery();
		
		// 设置查询关键字
		query.setQuery(keyword);
		// 设置分页条件
		query.setStart((page -1 ) * rows);
		query.setRows(rows);
		// 设置默认搜索域
		query.set("df", DEFAULT_FIELD);
		// 设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<span style='color:red'>");
		query.setHighlightSimplePost("</span>");
		SearchResult result = searchDao.search(query);
		
		int total = (int) result.getRecordCount();
		int totalPages = (total - 1) / rows + 1;
		result.setTotalPages(totalPages);
		return result;
	}

}
