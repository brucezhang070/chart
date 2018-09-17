package com.bright.chart.controller;

import com.bright.chart.dto.ChartContent;
import com.bright.chart.mq.consumer.ChartConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 控制类
 *
 * @author zhangyanqiang
 * @create 2018-09-06-下午3:50
 **/
@Controller
@Order(1)
public class ChartController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChartConsumer chartConsumer;

    @MessageMapping("/chart")
    @SendTo("/topic/chartHall")
    public ChartContent greeting(String name) throws Exception {
        //Thread.sleep(1000); // simulated delay
        ChartContent chartContent = new ChartContent();
        chartContent.setSpeaker(name);
        chartContent.setContent(name);
        return chartContent;
    }

    @MessageMapping("/chartPrivate/{userName}")
    //@SendToUser("/chartPrivate")
    public void handleChart(@DestinationVariable String userName, ChartContent chartContent){
        System.out.println("11111111");
        simpMessagingTemplate.convertAndSendToUser(chartContent.getListener(),"/chartPrivate",chartContent);
    }
}
