package com.x.flow.xflow.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * springboot集成mybatis的基本入口
 * 1）创建数据源
 * 2）创建SqlSessionFactory
 */
@Configuration    //该注解类似于spring配置文件
@MapperScan(basePackages="com.x.flow.xflow.mapper")
public class MyBatisConfig {
    
    @Autowired
    private Environment env;
    
    /**
     * 创建数据源
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错 
     */
//    @Bean
//    //@Primary
//    public DataSource getDataSource() throws Exception{
//        Properties props = new Properties();
//        props.put("driverClassName", env.getProperty("spring.datasource.driver-class-name"));
//        props.put("url", env.getProperty("spring.datasource.url"));
//        props.put("username", env.getProperty("spring.datasource.username"));
//        props.put("password", env.getProperty("spring.datasource.password"));
//        return DruidDataSourceFactory.createDataSource(props);
//    }

    /**
              * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dataSource);//指定数据源(这个必须有，否则报错)
        org.apache.ibatis.session.Configuration configuration =  new org.apache.ibatis.session.Configuration();
        configuration.setDefaultExecutorType(ExecutorType.REUSE);//开启重用prepare Statement
		fb.setConfiguration(configuration );
        //FIXME:下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        fb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));//指定基包
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapperLocations")));//指定xml文件位置
        
        return fb.getObject();
    }

}