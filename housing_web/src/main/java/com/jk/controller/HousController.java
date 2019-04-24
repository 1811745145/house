package com.jk.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk.config.ConstantUtil;
import com.jk.config.UserUtils;
import com.jk.pojo.GuanZhuBean;
import com.jk.pojo.HousBean;
import com.jk.service.HousService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("hous")
public class HousController {
    /**
     * 记录日志   当前类的日志
     */
    private static Logger log = LoggerFactory.getLogger(HousController.class);
    /**
     * 注入 service 层
     */
    @Autowired
    private HousService housService;
    /**
     * 注入 elasticsearch 模板工具
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    /**
     * 注入 RabbitMQ 模板工具
     */
    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 注入 mongodb 模板工具
     */
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 注入 redis 模板工具
     */
    @Autowired
    private RedisTemplate redisTemplate;
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
    @Async//开启异步处理 此方法会走spring线程池
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
        Integer userId = UserUtils.USER_ID;
        guanzhuBean.setUserId(userId);
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
     * 根据Id查看详情 第一次走 es 第二次走缓存 Redis
     * @param housBean
     * @return
     */
    @RequestMapping("queryProductById")
    @ResponseBody
    public HousBean queryProductById(HousBean housBean){
        String houseInfo = ConstantUtil.HOUSE_INFO+housBean.getId();
        Boolean aBoolean = redisTemplate.hasKey(houseInfo);
        if(aBoolean){
            //如果存在则取出list集合
            HousBean o = (HousBean) redisTemplate.opsForValue().get(houseInfo);
            System.out.println("走缓存");
            return o;
        }else{
            Client client = elasticsearchTemplate.getClient();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("hous").setTypes("housinfo").setQuery(QueryBuilders.matchQuery("id",housBean.getId()));
            SearchResponse searchResponse = searchRequestBuilder.get();
            SearchHits hits = searchResponse.getHits();
            Iterator<SearchHit> iterator = hits.iterator();
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
            HousBean housBean1 = JSON.parseObject(sourceAsString, HousBean.class);
            redisTemplate.opsForValue().set(houseInfo,housBean1);
            System.out.println("走elasticsearch");
            return housBean1;
        }
    }

    /**
     * 查询购物车
     */
    @RequestMapping("queryGuanzhu")
    @ResponseBody
    public List<GuanZhuBean> queryGuanzhu(GuanZhuBean guanZhuBean){
        //拿到用户id
        Integer userId = UserUtils.USER_ID;
        //查询条件
        Query query = new Query();
        //根据当前登录的用户Id进行查询当前用户关注的房源
        query.addCriteria(Criteria.where("userId").is(userId));
        if(!StringUtils.isEmpty(guanZhuBean.getHousTitle())){
            //跟你据小区标题模糊查询
            query.addCriteria(Criteria.where("housTitle").regex(guanZhuBean.getHousTitle()));
        }
        List<GuanZhuBean> guanZhuBeans = mongoTemplate.find(query, GuanZhuBean.class);
        return guanZhuBeans;
    }
    /**
     * 取消关注
     */
    @RequestMapping("deleteGuanzhuById")
    @ResponseBody
    public Boolean deleteGuanzhuById(GuanZhuBean guanZhuBean){
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(guanZhuBean.getId()));
            mongoTemplate.remove(query,GuanZhuBean.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 查看关注详情信息
     */
    @RequestMapping("queryProductByIdMongod")
    @ResponseBody
    public GuanZhuBean queryProductByIdMongod(GuanZhuBean guanZhuBean){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(guanZhuBean.getId()));
        GuanZhuBean one = mongoTemplate.findOne(query, GuanZhuBean.class);
        return one;
    }



}
