package com.bright.chart.mq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * 消息生产者
 *
 * @author zhangyanqiang
 * @create 2018-09-06-下午2:24
 **/
@Component
public class ChartProducer{
        /**
         * 生产者的组名
         */
        @Value("${apache.rocketmq.producer.chartProducerGroup}")
        private String producerGroup;

        /**
         * NameServer 地址
         */
        @Value("${apache.rocketmq.namesrvAddr}")
        private String namesrvAddr;

        @PostConstruct
        public void defaultMQProducer() {

            //生产者的组名
            DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

            //指定NameServer地址，多个地址以 ; 隔开
            producer.setNamesrvAddr(namesrvAddr);

            try {

                /**
                 * Producer对象在使用之前必须要调用start初始化，初始化一次即可
                 * 注意：切记不可以在每次发送消息时，都调用start方法
                 */
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            producer.start();
                        } catch (MQClientException e) {
                            e.printStackTrace();
                        }
                        //while (true){
                            //producer.createTopic("key_","PushTopic",i);
                            try {
                                Document doc = Jsoup.connect("https://news.baidu.com/").get();
                                Elements ListDiv = doc.getElementsByTag("li");
                                for (int j = 0 ; j < ListDiv.size(); j++) {

                                    Element td = ListDiv.get(j);

                                    String messageBody = td.getElementsByTag("a").text();
                                    Thread.sleep(1500);
                                    if (!StringUtils.isEmpty(messageBody)&&messageBody.trim().length()>4){
                                        String message = new String(messageBody.getBytes(), "utf-8");
                                        //构建消息
                                        Message msg = new Message("PushTopic", "push", "key_" , message.getBytes());
                                        SendResult result = producer.send(msg);

                                        System.out.println("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
                                    }

                                }

                            }catch (Exception e){

                            }
                        }
                   // }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                producer.shutdown();
            }

    }
}
