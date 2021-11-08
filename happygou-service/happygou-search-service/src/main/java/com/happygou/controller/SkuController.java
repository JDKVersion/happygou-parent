package com.happygou.controller;


import com.happygou.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result importData(){
        skuService.importEs();
        return new Result(true, StatusCode.OK,"导入数据到ES成功");
    }

    @GetMapping
    public Map<String,Object>search(@RequestParam(required = false) Map<String,Object>map){


        return skuService.search(map);
    }



}
