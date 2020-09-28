package com.example.springboot.Ceshi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class GetOutController {
    @Autowired
    private GetOutServiceImpl getOutServiceImpl;


    @PostMapping("openauth")
    @Scheduled(cron = "*/5 * * * * ?")
    public void getOut() throws Exception{
        getOutServiceImpl.openauth();
    }


    @Scheduled(cron = "*/5 * * * * ?")
    @PostMapping("sendLiuJia")
    public void sendLiuJia() throws Exception{
        getOutServiceImpl.sendLiuJia();
    }
}
