package com.happygou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 @ClassName: CanalApplication
 @Description: TODO
 @Author: Icon Sun
 @Date: 2021/10/28 13:26
 @Version: 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableCanalClient
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }


}
