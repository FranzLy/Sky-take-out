package com.sky.task;

import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CronTask {
    @Autowired
    private OrderService orderService;

    /**
     * 测试spring task
     */
//    @Scheduled(cron = "*/5 * * * * *")
//    public void testCronTask() {
//        log.info("cronTask");
//    }

    @Scheduled(cron = "0 * * * * *")
    public void everyMintueCronTask() {
        log.info("每分钟的定时任务:{}", LocalDateTime.now());

        // 处理超时订单
        orderService.processTimeOutOrder();
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
