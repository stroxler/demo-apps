package com.stroxler;

import com.stroxler.logging.LoggingInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;


@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void doWork() throws Exception {
    }

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }

    /*
     * This method is unnecessary (and actually causes problems in this
     * application due to more than one Properties bean existing) because of the
     * @PropertySource above, but I wanted to include it in the demo an example of
     * how to manually load a properties file as a bean.
     *
     * One reason having a manually-created @Bean rather than a magic annotation
     * can be really handy in practice is that you can use a different properties
     * file depending on environment variables, which is one simple way of
     * getting different behavior in dev, test, and prod modes
     * ------------------------------------------------------------------------
    @Bean
    public Properties applicationProperties() throws IOException {
        Properties props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
        return props;
    }
    */



}

