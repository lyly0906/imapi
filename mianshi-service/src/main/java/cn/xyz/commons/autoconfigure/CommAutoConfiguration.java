package cn.xyz.commons.autoconfigure;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.MongoConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.RedisConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.SmsConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter;

@Configuration
public class CommAutoConfiguration {
	@Autowired
	private KApplicationProperties config;
	
	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(
				new MappingFastjsonHttpMessageConverter());
	}
	

	@Bean(name="appConfig")
	public AppConfig appConfig(){
		AppConfig appConfig=config.getAppConfig();
		System.out.println("appConfig>>UploadDomain----"+appConfig.getUploadDomain());
		return appConfig;
	}
	@Bean(name="smsConfig")
	public SmsConfig smsConfig(){
		SmsConfig smsConfig=config.getSmsConfig();
		return smsConfig;
	}
	@Bean(name="xmppConfig")
	public XMPPConfig xmppConfig(){
		XMPPConfig xmppConfig=config.getXmppConfig();
		System.out.println("xmppConfig>>Host----"+xmppConfig.getHost());
		return xmppConfig;
	}
	@Bean(name="mongoConfig")
	public MongoConfig mongoConfig(){
		MongoConfig mongoConfig=config.getMongoConfig();
		return mongoConfig;
	}
	@Bean(name="redisConfig")
	public RedisConfig redisConfig(){
		RedisConfig redisConfig=config.getRedisConfig();
		return redisConfig;
	}
	
	
	/*@Bean(name="schedulerFactory")
	public SchedulerFactory schedulerFactory(){
		return (SchedulerFactory) context.getBean("schedulerFactory");
	} 
	@Bean(name="scheduler")
	public Scheduler scheduler() throws SchedulerException{
		return schedulerFactory().getScheduler();
	} */
	
	
	
}
