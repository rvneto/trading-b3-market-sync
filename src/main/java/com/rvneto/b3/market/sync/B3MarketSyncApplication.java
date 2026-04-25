package com.rvneto.b3.market.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class B3MarketSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(B3MarketSyncApplication.class, args);
    }

}
