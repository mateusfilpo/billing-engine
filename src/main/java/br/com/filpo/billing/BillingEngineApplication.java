package br.com.filpo.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BillingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingEngineApplication.class, args);
    }

}