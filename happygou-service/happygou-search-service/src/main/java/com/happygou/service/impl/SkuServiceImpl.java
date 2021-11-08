package com.happygou.service.impl;

import com.alibaba.fastjson.JSON;
import com.happygou.dao.SkuEsMapper;
import com.happygou.goods.feign.SkuFeign;
import com.happygou.goods.pojo.Category;
import com.happygou.goods.pojo.Sku;
import com.happygou.search.pojo.SkuInfo;
import com.happygou.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private SkuFeign skuFeign;


    // 实现索引库的增删改查的对象
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public void importEs() {

        Result<List<Sku>> listResult = skuFeign.findAll();
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(listResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);

        }
        skuEsMapper.saveAll(skuInfoList);
    }

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        //构建查询条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);

        // 集合搜索 查询所有skuInfo集合
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);

        //分类分组查询 (根据skuInfo中的categoryName域进行分类)
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category"))) {

            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
            resultMap.put("categoryList", categoryList);

        }

        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand"))) {

            //品牌分组查询(根据skuInfo中的brandName进行分类)
            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
            resultMap.put("brandList", brandList);
        }


        //规格查询
        Map<String, Set<String>> allSpecMap = searchSpecList(nativeSearchQueryBuilder);

        resultMap.put("allSpecMap", allSpecMap);
        return resultMap;
    }

    //查询skuInfo中的所有规格信息

    //1,根据spec中的spec.keyword进行分组，目的是防止两条规格信息完全相同的记录，并取到所有spec记录list
    public Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(30000));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = new ArrayList<>();
        return putAllSpec(stringTerms, specList);
    }

    /*
     * 规格汇总合并
     * */
    public Map<String, Set<String>> putAllSpec(StringTerms stringTerms, List<String> specList) {
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String specName = bucket.getKeyAsString();
            specList.add(specName);
        }
        Map<String, Set<String>> stringSetMap = new HashMap<>();
        for (String spec : specList) {
            Map<String, Object> mapSpec = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, Object> entry : mapSpec.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();
                Set<String> set = stringSetMap.get(key);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(value);
                stringSetMap.put(key, set);
            }
        }
        return stringSetMap;
    }
    //2,观察发现list中的每一条记录都可以转成 Map(String,String)对象，转换

    //3,

    //4,将转换的每一条对象合并成一个Map<String,Set<String>>的值


    public NativeSearchQueryBuilder buildBasicQuery(Map<String, Object> searchMap) {
        // 创建条件查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 如果关键字不为空，则搜索出的数据必须包含关键字
        if (searchMap != null && searchMap.size() > 0) {
            //封装查询条件
            String keywords = (String) searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }

            // 输入了分类
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }
            // 如果输入了品牌
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }
            for (Map.Entry entry : searchMap.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("spec_")) {
                    String value = (String) entry.getValue();
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value));
                }
                String price = (String) searchMap.get("price");
                if (!StringUtils.isEmpty(price)) {
                    price = price.replace("元", "").replace("以上", "");
                    String[] prices = price.split("-");
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    if (prices.length > 1) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(Integer.parseInt(prices[1])));
                    }
                }
            }
        }
        //排序实现
        String sorttField = (String) searchMap.get("sorttField");// 指定排序的域
        String sortRule = (String) searchMap.get("sortRule");// 指定排序规则
        if(!StringUtils.isEmpty(sortRule)&&!StringUtils.isEmpty(sorttField)){
            nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sorttField).order(SortOrder.valueOf(sortRule)));
        }
        // 分页
        Integer pageNum = coverterPage(searchMap);
        int pageSize=3;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,pageSize));
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    public Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 执行搜索，并返回结果集
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        int pages = page.getTotalPages();
        long count = page.getTotalElements();
        List<SkuInfo> list = page.getContent();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("pages", pages);
        resultMap.put("list", list);
        resultMap.put("count", count);
        return resultMap;
    }

    public List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /*
         *
         *根据分类名称进行分组
         * 添加一个聚合操作
         * 第二个方法是根据哪个域进行分组
         * 第一个方法是给域取个别名
         * */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> skuInfoAggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        StringTerms stringTerms = skuInfoAggregatedPage.getAggregations().get("skuCategory");

        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }
        return categoryList;
    }


    public List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /*
         *
         *根据名称品牌进行分组
         * 添加一个聚合操作
         * 第二个方法是根据哪个域进行分组
         * 第一个方法是给域取个别名
         * */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        AggregatedPage<SkuInfo> skuInfoAggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        StringTerms stringTerms = skuInfoAggregatedPage.getAggregations().get("skuBrand");

        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {

            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }
        return brandList;
    }

    public Integer coverterPage(Map<String,Object> searchMap){
        if(searchMap!=null){
            String pageNum = (String) searchMap.get("pageNum");
            try {
                return Integer.parseInt(pageNum);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
