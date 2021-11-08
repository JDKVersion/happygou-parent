package com.happygou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 @ClassName: FileApploication
 @Description: TODO
 @Author: Icon Sun
 @Date: 2021/10/21 19:43
 @Version: 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class,args);
    }


}
