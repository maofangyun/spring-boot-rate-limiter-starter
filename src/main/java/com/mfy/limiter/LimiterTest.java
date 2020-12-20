package com.mfy.limiter;

import com.mfy.limiter.annotation.LimiterScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@LimiterScan(value = {"com.mfy.limiter.filter"})
public class LimiterTest {
    public static void main(String[] args) {
        SpringApplication.run(LimiterTest.class,args);
    }
}
