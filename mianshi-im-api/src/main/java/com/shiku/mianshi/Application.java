package com.shiku.mianshi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.google.common.collect.Maps;
import com.shiku.mianshi.filter.AuthorizationFilter;

import cn.xyz.commons.autoconfigure.KMongoAutoConfiguration;
import cn.xyz.commons.autoconfigure.KRedisAutoConfiguration;
import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;

//@EnableScheduling
@Configuration
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, RedisAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan({ "cn.xyz", "com.shiku" })
public class Application extends SpringBootServletInitializer {
	@Autowired
	private KAdminProperties props;

	
	@Resource(name = "appConfig")
	public AppConfig appConfig;
	
	public static void main(String... args) {
		SpringApplication.run(Application.class, args);
		
		
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(new Class[] { Application.class });
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		AuthorizationFilter filter = new AuthorizationFilter();
		Map<String, String> initParameters = Maps.newHashMap();
		initParameters.put("enable", "true");
		List<String> urlPatterns = Arrays.asList("/*");

		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(filter);
		registrationBean.setInitParameters(initParameters);
		registrationBean.setUrlPatterns(urlPatterns);
		return registrationBean;
	}

	@Bean(name = "viewResolver")
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/pages/");
		viewResolver.setSuffix(".jsp");

		return viewResolver;
	}

	@Bean(name = "adminMap")
	public Map<String, String> adminMap() {
		Map<String, String> adminMap = Maps.newHashMap();
		String[] users = props.getUsers().split(",");
		for (String t : users) {
			String[] user = t.split(":");
			System.out.println(user);
			adminMap.put(user[0], user[1]);

			System.out.println("加载管理员帐号：" + user[0]);
		}
		return adminMap;
	}

}
