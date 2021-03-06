package com.example.demo12kaptcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource(locations = {"classpath:kaptchaConfig.xml"})
public class KaptchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaptchaApplication.class, args);
    }

}
