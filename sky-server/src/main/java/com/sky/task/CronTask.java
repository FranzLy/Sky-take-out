package com.sky.task;

import com.alibaba.fastjson.JSONObject;
import com.sky.service.OrderService;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CronTask {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 测试spring task
     */
//    @Scheduled(cron = "*/5 * * * * *")
//    public void testCronTask() {
//        log.info("cronTask");
//    }

    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void every5SecTask() {
        log.info("每5秒钟的定时任务：{}", LocalDateTime.now());

        webSocketServer.sendToAllClient("这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }

    @Scheduled(cron = "0 * * * * *")
    public void everyMintueCronTask() {
        log.info("每分钟的定时任务:{}", LocalDateTime.now());

        // 处理超时订单
        orderService.processTimeOutOrder();
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void everyHourCronTask(){
        log.info("每小时触发:{}", LocalDateTime.now());


        //因为实际上没法调用微信支付成功，所以在这里模拟一下支付成功，推送来单提醒的代码
        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", 23);
        map.put("content", "订单号" +16715906178450L);
        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 每天凌晨1点触发
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void oneAmCronTask(){
        log.info("每天凌晨1点触发:{}", LocalDateTime.now());

        orderService.processInDeliveryOrder();
    }
}
