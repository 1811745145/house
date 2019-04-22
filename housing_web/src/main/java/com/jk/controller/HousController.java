package com.jk.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk.pojo.GuanZhuBean;
import com.jk.pojo.HousBean;
import com.jk.service.HousService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("hous")
public class HousController {
    @Autowired
    private HousService housService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 发送消息  发出Id
     * @param id
     * @return
     */
    @RequestMapping("guanZhuToId")
    @ResponseBody
    public Boolean guanZhuToId(Integer id){
        try {
            amqpTemplate.convertAndSend("hous", id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 处理消息 增到 mongod 为我的关注
     * @param map
     */
    @RabbitListener(queues = "hous")
    public void consoumers(Integer id){
        Client client = elasticsearchTemplate.getClient();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("hous").setTypes("housinfo").setQuery(QueryBuilders.matchQuery("id",id));
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        SearchHit next = iterator.next();
        String sourceAsString = next.getSourceAsString();
        GuanZhuBean guanzhuBean = JSON.parseObject(sourceAsString, GuanZhuBean.class);
        mongoTemplate.save(guanzhuBean);
        System.out.println(guanzhuBean);
    }
    /**
     * 查询以及条查
     * @param housBean
     * @return
     */
    @RequestMapping("queryProduct")
    @ResponseBody
    public List<HousBean> queryProduct(HousBean housBean,String sortDesc){
        Client client = elasticsearchTemplate.getClient();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("hous").setTypes("housinfo");
        if(housBean.getHousTitle() !=null && housBean.getHousTitle() != "" ){
            searchRequestBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("houstitle", housBean.getHousTitle())));
        }
        if(sortDesc !=null && sortDesc !=""){
            searchRequestBuilder.addSort("housprice", SortOrder.DESC);
        }else{
            searchRequestBuilder.addSort("housprice", SortOrder.ASC);
        }
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("houstitle");
        highlightBuilder.preTags("<font color='red' >");
        highlightBuilder.postTags("</font>");
        searchRequestBuilder.highlighter(highlightBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        List<HousBean> list = new ArrayList<HousBean>();
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            Map<String, HighlightField> highlightFields = next.getHighlightFields();
            String sourceAsString = next.getSourceAsString();
            HighlightField info = highlightFields.get("houstitle");
            HousBean housBean1 = JSON.parseObject(sourceAsString, HousBean.class);
            //取得定义的高亮标签
            if(info !=null) {
                Text[] fragments = info.fragments();
                //为thinkName（相应字段）增加自定义的高亮标签
                String title = "";
                for (Text text1 : fragments) {
                    title += text1;
                }
                housBean1.setHousInfo(title);
            }
            list.add(housBean1);
        }
        return list;
    }
    /**
     * 详情
     * @param housBean
     * @return
     */
    @RequestMapping("queryProductById")
    @ResponseBody
    public HousBean queryProductById(HousBean housBean){
        Client client = elasticsearchTemplate.getClient();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("hous").setTypes("housinfo").setQuery(QueryBuilders.matchQuery("id",housBean.getId()));
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
            HousBean housBean1 = JSON.parseObject(sourceAsString, HousBean.class);
        return housBean1;
    }
}
