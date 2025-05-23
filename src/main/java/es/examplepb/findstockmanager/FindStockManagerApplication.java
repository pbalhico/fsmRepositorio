package es.examplepb.findstockmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FindStockManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindStockManagerApplication.class, args);
    }

}
