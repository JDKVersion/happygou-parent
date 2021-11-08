package com.happygou.dao;

import com.happygou.goods.pojo.OrderItem;
import com.happygou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {

    @Update(value="update tb_sku set num=num-#{num},sale_num=sale_num+#{num} where id =#{skuId} and num >=#{num}")
    public int decrCount(OrderItem orderItem);
}
