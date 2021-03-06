package com.happygou.goods.feign;

import com.happygou.goods.pojo.OrderItem;
import com.happygou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.goods.feign *
 * @since 1.0
 */
@FeignClient(value="goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /**
     * 查询符合条件的状态的SKU的列表
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);


    /**
     * 根据条件搜索的SKU的列表
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);


    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name = "id") Long id);


    @GetMapping
    public Result<List<Sku>> findAll();

    @PostMapping(value = "/decr/count")
    public Result decrCount(@RequestBody OrderItem orderItem);



}
