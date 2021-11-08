package com.happygou.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

/**
 @ClassName: CanalDataEventListener
 @Description: TODO
 @Author: Icon Sun
 @Date: 2021/10/29 12:01
 @Version: 1.0
 **/
@CanalEventListener
public class CanalDataEventListener {

/*
* 监听数据增加
* @InsertListenerPoint 增加监听数据的注解
* CanalEntry.EntryType:增加数据的类型
*CanalEntry.Data:发生变更的那一行数据
*
* */
@InsertListenPoint
public void onEventInsert(CanalEntry.EntryType entryType,CanalEntry.RowData rowData){
    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
     // 业务逻辑
    }
}

/*
* 监听数据修改
* */
@UpdateListenPoint
public void onEventUpdate(CanalEntry.EntryType entryType,CanalEntry.RowData rowData){
    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
        //修改之前的业务逻辑
    }


    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
        //修改之后的业务逻辑
    }
}


/*
* 监听数据删除
* */
@DeleteListenPoint
public void onEventDelete(CanalEntry.EntryType entryType,CanalEntry.RowData rowData){
    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
        // 删除之后的业务逻辑
    }

}

/*
* 自定义监听
*
* */
@ListenPoint(
        destination = "example", // 监听那个数据库实例
        eventType = {CanalEntry.EventType.UPDATE,
                     CanalEntry.EventType.DELETE},//监听类型
        schema = {"changgou_content"}, // 监听哪一个数据库

        table = {"tb_content"} // 监听哪一张表
                     )
public void onEventCustomListener(CanalEntry.EntryType entryType,CanalEntry.RowData rowData){

    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
        // 监听之前的数据
    }

    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
        // 监听之后的数据
    }
}

}
