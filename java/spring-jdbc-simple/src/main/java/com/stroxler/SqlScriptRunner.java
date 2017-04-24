package com.stroxler;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;


public class SqlScriptRunner {

    public static void runSqlScript(String scriptPath, JdbcTemplate jdbcTemplate) {
        ClassPathResource scriptResource = new ClassPathResource(scriptPath);
        ResourceDatabasePopulator dbInitializer = new ResourceDatabasePopulator(scriptResource);
        dbInitializer.execute(jdbcTemplate.getDataSource());
    }

}
