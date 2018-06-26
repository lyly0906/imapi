package cn.xyz.commons.autoconfigure;

import java.util.List;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import cn.xyz.mianshi.vo.Language;



@Component
@ConfigurationProperties(prefix = "im")
public class KApplicationProperties {
	// ,locations="classpath:application-test.properties"  //本地链接公网ip的测试 配置
	//  ,locations="classpath:application-local.properties"  //本地链接内网ip的测试 配置
	////application
	
	
	public KApplicationProperties() {
		// TODO Auto-generated constructor stub
	}
	private PoolProperties poolConfig;
	private MongoConfig mongoConfig;
	private RedisConfig redisConfig;
	private XMPPConfig xmppConfig;
	private Open189Config open189Config;
	private MySqlConfig mySqlConfig;
	private AppConfig appConfig;
	private SmsConfig smsConfig;

	
	public MySqlConfig getMySqlConfig() {
		return mySqlConfig;
	}

	public void setMySqlConfig(MySqlConfig mySqlConfig) {
		this.mySqlConfig = mySqlConfig;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public PoolProperties getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(PoolProperties poolConfig) {
		this.poolConfig = poolConfig;
	}

	public MongoConfig getMongoConfig() {
		return mongoConfig;
	}

	public void setMongoConfig(MongoConfig mongoConfig) {
		this.mongoConfig = mongoConfig;
	}

	public RedisConfig getRedisConfig() {
		return redisConfig;
	}

	public void setRedisConfig(RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}

	public XMPPConfig getXmppConfig() {
		return xmppConfig;
	}

	public void setXmppConfig(XMPPConfig xmppConfig) {
		this.xmppConfig = xmppConfig;
	}

	public Open189Config getOpen189Config() {
		return open189Config;
	}

	public void setOpen189Config(Open189Config open189Config) {
		this.open189Config = open189Config;
	}

	

	
	public SmsConfig getSmsConfig() {
		return smsConfig;
	}

	public void setSmsConfig(SmsConfig smsConfig) {
		this.smsConfig = smsConfig;
	}




	public static class MongoConfig {
		private List<String> host;
		private List<Integer> port;
		private String dbName;
		//配置是否使用集群模式   读写分离    0 单机 模式     1：集群模式
		private int cluster=0;
		private String username;
		private String password;
		private String url;

		public List<String> getHost() {
			return host;
		}

		public void setHost(List<String> host) {
			this.host = host;
		}

		public List<Integer> getPort() {
			return port;
		}

		public void setPort(List<Integer> port) {
			this.port = port;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getCluster() {
			return cluster;
		}

		public void setCluster(int cluster) {
			this.cluster = cluster;
		}

	}

	public static class RedisConfig {
		private int database=0;
		private String host;
		private int port;
	
		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getDatabase() {
			return database;
		}

		public void setDatabase(int database) {
			this.database = database;
		}

	}

	public static class XMPPConfig {
		private String host;
		private int port;
		private String username;
		private String password;
		private String dbhost;
		private String domain;
		private int dbport;
		private String dbName;
		private String dbUsername;
		private String dbPassword;
        private String authorization;
		private String add_user;
		private String edit_user;
		private String del_user;
		private String send_message;

		public String getAuthorization() { return authorization; }

		public void setAuthorization(String authorization) { this.authorization = authorization; }

		public String getAddUser() { return add_user; }

		public void setAddUser(String add_user) { this.add_user = add_user; }

		public String getEditUser() { return edit_user; }

		public void setEditUser(String edit_user) { this.edit_user = edit_user; }

		public String getDelUser() { return del_user; }

		public void setDelUser(String del_user) { this.del_user = del_user; }

		public String getSendMessage() { return send_message; }

		public void setSendMessage(String send_message) { this.send_message = send_message; }

		public String getDbhost() {
			return dbhost;
		}

		public void setDbhost(String dbhost) {
			this.dbhost = dbhost;
		}

		public int getDbport() {
			return dbport;
		}

		public void setDbport(int dbport) {
			this.dbport = dbport;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public String getDbUsername() {
			return dbUsername;
		}

		public void setDbUsername(String dbUsername) {
			this.dbUsername = dbUsername;
		}

		public String getDbPassword() {
			return dbPassword;
		}

		public void setDbPassword(String dbPassword) {
			this.dbPassword = dbPassword;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	public static class Open189Config {
		private String app_id;
		private String app_secret;
		private String app_template_id_invite;
		private String app_template_id_random;
		private String template_id;

		public String getApp_id() {
			return app_id;
		}

		public void setApp_id(String app_id) {
			this.app_id = app_id;
		}

		public String getApp_secret() {
			return app_secret;
		}

		public void setApp_secret(String app_secret) {
			this.app_secret = app_secret;
		}

		public String getApp_template_id_invite() {
			return app_template_id_invite;
		}

		public void setApp_template_id_invite(String app_template_id_invite) {
			this.app_template_id_invite = app_template_id_invite;
		}

		public String getApp_template_id_random() {
			return app_template_id_random;
		}

		public void setApp_template_id_random(String app_template_id_random) {
			this.app_template_id_random = app_template_id_random;
		}

		public String getTemplate_id() {
			return template_id;
		}

		public void setTemplate_id(String template_id) {
			this.template_id = template_id;
		}
	}
	
	
	public static class MySqlConfig {
		private String url;
		private String user;
		private String password;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
		
	}
	
	public static class AppConfig{
		private String uploadDomain="http://upload.shiku.co";//上传服务器域名
		private List<Language> languages; //语言
		private int openTask=1;//是否开启定时任务
		private int distance=20;
		public String getUploadDomain() {
			return uploadDomain;
		}

		public void setUploadDomain(String uploadDomain) {
			this.uploadDomain = uploadDomain;
		}

		public int getOpenTask() {
			return openTask;
		}

		public void setOpenTask(int openTask) {
			this.openTask = openTask;
		}

		public List<Language> getLanguages() {
			return languages;
		}

		public void setLanguages(List<Language> languages) {
			this.languages = languages;
		}

		public int getDistance() {
			return distance;
		}

		public void setDistance(int distance) {
			this.distance = distance;
		}
	}
	public static class SmsConfig {
		
		private int openSMS=1;//是否发送短信验证码
		private String host;
		private int port;
		private String api;
		private String username;//短信平台用户名
		private String password;////短信平台密码
		
		public int getOpenSMS() {
			return openSMS;
		}
		public void setOpenSMS(int openSMS) {
			this.openSMS = openSMS;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public String getApi() {
			return api;
		}
		public void setApi(String api) {
			this.api = api;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
	}
}
