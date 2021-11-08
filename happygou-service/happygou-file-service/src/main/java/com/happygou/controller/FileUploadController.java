package com.happygou.controller;

import com.happygou.file.FastDFSFile;
import com.happygou.util.FastDFSClient;
import entity.BaseExceptionHandler;
import entity.Result;
import entity.StatusCode;
import io.netty.util.internal.StringUtil;
import org.csource.common.MyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 @ClassName: FileUploadController
 @Description: TODO
 @Author: Icon Sun
 @Date: 2021/10/21 20:37
 @Version: 1.0
 **/
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUploadController extends BaseExceptionHandler {
    /*
    * 文件上传
    * */
    @PostMapping
    public Result upload(@RequestParam(value = "file")MultipartFile multipartFile) throws IOException, MyException {

        FastDFSFile fastDFSFile = new FastDFSFile(
                multipartFile.getOriginalFilename(),
                multipartFile.getBytes(),
                StringUtils.getFilenameExtension( multipartFile.getOriginalFilename()));
        String[] uploads=FastDFSClient.upload(fastDFSFile);
        // url:文件的访问路径：ip+端口+group+/+group上的文件名
        String url=FastDFSClient.getTrackerInfo()+"/"+uploads[0]+"/"+uploads[1];
        return new Result(true, StatusCode.OK,"文件上传成功",url);
    }

}
