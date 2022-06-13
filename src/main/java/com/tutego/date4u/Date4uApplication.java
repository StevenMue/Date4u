package com.tutego.date4u;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
@EnableJpaAuditing
@EnableTransactionManagement
public class Date4uApplication {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        ApplicationContext ctx = SpringApplication.run( Date4uApplication.class,
                args );
        Arrays.stream( ctx.getBeanDefinitionNames() )
                .sorted()
                .forEach( log::info );
    }
}
