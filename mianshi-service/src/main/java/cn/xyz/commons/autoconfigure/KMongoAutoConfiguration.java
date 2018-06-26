package cn.xyz.commons.autoconfigure;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.xyz.commons.autoconfigure.KApplicationProperties.MongoConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.User;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class KMongoAutoConfiguration {
	private Morphia morphiaForTigase;
	private Datastore dsForTigase;

	private Morphia morphia;
	private Datastore ds;

	private MongoClient mongoClient;
	private MongoClient tigMongoClient;

	@Resource
	private MongoConfig mongoConfig;
	@Resource
	private XMPPConfig xmppCConfig;

	@PreDestroy
	public void close() {
		if (null != mongoClient)
			mongoClient.close();
	}

	@Bean(name = "mongoClient", destroyMethod = "close")
	public MongoClient mongo() throws UnknownHostException {
		List<ServerAddress> seeds = Lists.newArrayList();
		ServerAddress address=null;
		for (int i = 0; i < mongoConfig.getHost().size(); i++) {
			address=new ServerAddress(mongoConfig.getHost().get(i), mongoConfig.getPort().get(i));
			seeds.add(address);
			System.out.println("MongoDB host    "+address.getHost()+"  port  "+address.getPort());
		}
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		builder.socketKeepAlive(true);
		builder.socketTimeout(2000);
		builder.maxWaitTime(2000);
		builder.heartbeatFrequency(2000);// 心跳频率

		MongoClientOptions options= builder.build();
		 
		 MongoCredential credential =null;
		 //是否配置了密码
		 if(!StringUtil.isEmpty(mongoConfig.getUsername())&&!StringUtil.isEmpty(mongoConfig.getPassword()))
			 credential = MongoCredential.createScramSha1Credential(mongoConfig.getUsername(), mongoConfig.getDbName(), 
					 mongoConfig.getPassword().toCharArray());
		if(null==credential)
			mongoClient = new MongoClient(seeds,options);
		else 
			mongoClient = new MongoClient(seeds,Arrays.asList(credential),options);
		// this.mongo.setReadPreference(ReadPreference.);
		return mongoClient;

	}

	@Bean(name = "morphiaForTigase")
	public Morphia morphiaForTigase() {
		morphiaForTigase = new Morphia();
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Member.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Notice.class);

		return morphiaForTigase;
	}
/*
	@Bean(name = "morphia")
	public Morphia morphia() {
		// IMPORTANT SPRING-BOOT JAR 应用在LINUX系统下时，mapPackage无法加载jar文件中的类
		// 详见ReflectionUtils类486、487行
		//
		morphia = new Morphia();
		// morphia.mapPackage("com.shiku.mianshi.vo", false);
		// 手动加载
		if (0 == morphia.getMapper().getMappedClasses().size()) {
			morphia.map(cn.xyz.mianshi.vo.AuditionFT.class);
			morphia.map(cn.xyz.mianshi.vo.AuditionRT.class);
			morphia.map(cn.xyz.mianshi.vo.CheckVO.class);
			morphia.map(cn.xyz.mianshi.vo.Comment.class);

			morphia.map(cn.xyz.mianshi.vo.CompanyFans.class);
			morphia.map(cn.xyz.mianshi.vo.CompanyFriends.class);

			morphia.map(cn.xyz.mianshi.vo.Fans.class);
			morphia.map(cn.xyz.mianshi.vo.Friends.class);
			morphia.map(cn.xyz.mianshi.vo.Gift.class);
			morphia.map(cn.xyz.mianshi.vo.JobApply.class);
			morphia.map(cn.xyz.mianshi.vo.JobVO.class);

			morphia.map(cn.xyz.mianshi.vo.Msg.class);
			morphia.map(cn.xyz.mianshi.vo.NoticeVO.class);
			morphia.map(cn.xyz.mianshi.vo.Praise.class);
			morphia.map(cn.xyz.mianshi.vo.User.class);

			morphia.map(cn.xyz.mianshi.vo.UserCollect.class);
		}

		return morphia;
	}
	
	*
	*/
	
	
	@Bean(name = "morphia")
	public Morphia morphia() {
		morphia = new Morphia();
		// morphia.mapPackage("com.uvwxyz.intv.vo", true);
		mapPackage("cn.xyz.mianshi.vo", morphia);
		return morphia;
	}

	@Bean(name = "dsForRW")
	public Datastore dsForRW(MongoClient mongoClient) {
		ds = morphia().createDatastore(mongoClient, mongoConfig.getDbName());
		ds.ensureIndexes();
		ds.ensureCaps();
		try {
			BasicDBObject keys = new BasicDBObject();
			keys.put("coordinate", "2d");
			keys.put("nickname", 1);
			keys.put("sex", 1);
			keys.put("birthday", 1);
			keys.put("active", 1);

			DBCollection dbCollection = ds.getCollection(User.class);
			dbCollection.createIndex(keys);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public Datastore dsForRead(MongoClient mongoClient) {
		// TODO MONGODB、只读库实现
		return null;
	}

	public Datastore dsForWrite(MongoClient mongoClient) {
		// TODO MONGODB、只写库实现
		return null;
	}
	
	@Bean(name = "dsForTigase")
	public Datastore dsForTigase() throws Exception {
		System.out.println("调用mongodb数据库："+ xmppCConfig.getDbName());
		dsForTigase = morphiaForTigase().createDatastore(getTigMongoClient(), xmppCConfig.getDbName());
		dsForTigase.ensureIndexes();
		dsForTigase.ensureCaps();

		return dsForTigase;
	}
	
	private MongoClient getTigMongoClient() throws Exception{
		if(null==tigMongoClient){
			List<ServerAddress> seeds = Lists.newArrayList(new ServerAddress(
					xmppCConfig.getDbhost(), xmppCConfig.getDbport()));

			MongoClientOptions.Builder builder = MongoClientOptions.builder();
			builder.socketKeepAlive(true);
			builder.socketTimeout(2000);
			builder.maxWaitTime(2000);
			builder.heartbeatFrequency(2000);// 心跳频率

			MongoClientOptions options= builder.build();
			 
			 MongoCredential credential =null;
			 //是否配置了密码
			 if(!StringUtil.isEmpty(xmppCConfig.getDbUsername())&&!StringUtil.isEmpty(xmppCConfig.getDbPassword()))
				 credential = MongoCredential.createScramSha1Credential(xmppCConfig.getDbUsername(), xmppCConfig.getDbName(), 
						 xmppCConfig.getDbPassword().toCharArray());
			if(null==credential)
				tigMongoClient = new MongoClient(seeds,options);
			else 
				tigMongoClient = new MongoClient(seeds,Arrays.asList(credential),options);
			// this.mongo.setReadPreference(ReadPreference.);
			return tigMongoClient;
		}else
			return tigMongoClient;
	}
	
	private void mapPackage(String packageName, Morphia morphia) {
		try {
			String name = packageName.replace('.', '/');
			URL url = Thread.currentThread().getContextClassLoader().getResource(name);
			String[] names = url.toString().split("!");
			if (1 == names.length) {
				File packagePath = new File(names[0].replace("file:/", ""));
				if (packagePath.isDirectory()) {
					File[] files = packagePath.listFiles();
					for (File file : files) {
						if (file.getName().endsWith(".class") && -1 == file.getName().indexOf("$")) {
							String className = packageName + "."
									+ file.getName().replace('/', '.').replace(".class", "");
							Class<?> cls = Class.forName(className);
							if (null != cls.getAnnotation(Entity.class)) {
								morphia.map(Class.forName(className));
								System.out.println("mapPackage：" + className);
							}
						}
					}
				}
			} else if (3 == names.length) {
				String warName = names[0].replace("jar:file:", "");
				String jarName = names[1].substring(1);
				String packagePath = names[2].substring(1);

				// 获取war包对象
				JarFile jarFile = new JarFile(new File(warName));
				// 获取jar在war中的位置
				JarEntry ze = jarFile.getJarEntry(jarName);
				// 获取war包中的jar数据
				InputStream in = jarFile.getInputStream(ze);
				//
				JarInputStream is = new JarInputStream(in);
				JarEntry je;
				while (null != (je = is.getNextJarEntry())) {
					if (je.getName().startsWith(packagePath)) {
						if (je.getName().endsWith(".class") && -1 == je.getName().indexOf("$")) {
							String className = je.getName().replace('/', '.').replace(".class", "");
							Class<?> cls = Class.forName(className);
							if (null != cls.getAnnotation(Entity.class)) {
								morphia.map(Class.forName(className));
								System.out.println("mapPackage：" + className);
							}
						}
					}
				}
				is.closeEntry();
				is.close();
				jarFile.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
