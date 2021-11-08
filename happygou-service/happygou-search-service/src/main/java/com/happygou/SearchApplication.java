package com.happygou;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 @ClassName: SearchApplication
 @Description: TODO
 @Author: Icon Sun
 @Date: 2021/10/30 22:39
 @Version: 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.happygou.goods.feign"})
@EnableElasticsearchRepositories(basePackages = "com.happygou.dao")
public class SearchApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(SearchApplication.class,args);
    }

}
