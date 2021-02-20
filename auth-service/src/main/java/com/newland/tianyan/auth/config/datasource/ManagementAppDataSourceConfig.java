package com.newland.tianyan.auth.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.newland.oauth.mapper.app", sqlSessionTemplateRef = "managementAppSqlSessionTemplate")
public class ManagementAppDataSourceConfig {

//    @Primary
    @Bean(name = "managementAppDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.management-app")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

//    @Primary
    @Bean(name = "managementAppTransactionManager")
    public DataSourceTransactionManager accountTransactionManager(@Qualifier("managementAppDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Primary
    @Bean(name = "managementAppSqlSessionFactory")
    public SqlSessionFactory accountSqlSessionFactory(@Qualifier("managementAppDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis/mybatis-config.xml"));
        return bean.getObject();
    }

//    @Primary
    @Bean(name = "managementAppSqlSessionTemplate")
    public SqlSessionTemplate accountSqlSessionTemplate(@Qualifier("managementAppSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
