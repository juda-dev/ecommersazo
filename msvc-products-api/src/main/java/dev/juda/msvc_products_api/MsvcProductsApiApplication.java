package dev.juda.msvc_products_api;

import dev.juda.libs_msvc_commons.domain.messaging.ReplyInbox;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsvcProductsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcProductsApiApplication.class, args);
    }

    @Bean
    public ReplyInbox inbox() {
        return new ReplyInbox();
    }
}
