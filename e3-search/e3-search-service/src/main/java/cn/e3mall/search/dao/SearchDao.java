package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

/**
 * 商品搜索dao
 * @version 1.0
 */
@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;

	/**
	 *根据查询条件查询索引库
	 * @param query
	 * @return
	 */
	public SearchResult search(SolrQuery query) throws Exception {
		QueryResponse response = solrServer.query(query);
		SolrDocumentList solrDocumentList = response.getResults();
		long numFound = solrDocumentList.getNumFound();
		SearchResult result = new SearchResult();
		result.setRecordCount(numFound);
		
		List<SearchItem> itemList = new ArrayList<>();
		
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		for (SolrDocument searchItem : solrDocumentList) {
			SearchItem item = new SearchItem();
			item.setId((String) searchItem.get("id"));
			item.setCategory_name((String) searchItem.get("item_category_name"));
			item.setImage((String) searchItem.get("item_image"));
			item.setPrice((long) searchItem.get("item_price"));
			item.setSell_point((String) searchItem.get("item_sell_point"));
			
			// 获取高亮结果，先取小Map再去List
			List<String> list = highlighting.get(searchItem.get("id")).get("item_title");;
			String itemTitle = "";
			if (list != null && list.size() > 0) {
				itemTitle = list.get(0);
			} else {
				itemTitle = (String) searchItem.get("item_title");
			}
			item.setTitle(itemTitle);
			itemList.add(item);
		}
		result.setItemList(itemList);
		return result;
	}
	
}
