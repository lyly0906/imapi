package cn.xyz.mianshi.service.impl;

import java.util.List;

import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.BeanUtils;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.vo.Config;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MongoRepository;
import cn.xyz.repository.mongo.UserRepositoryImpl;
import cn.xyz.service.KXMPPServiceImpl;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Service()
public class AdminManagerImpl extends MongoRepository implements AdminManager {

	
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Autowired
	private UserManager userManager;



	@Override
	public Config getConfig() {
		Config config=null;
		config=null; //KSessionUtil.getConfig();
		if(null==config){
			config = dsForRW.createQuery(Config.class).field("_id").notEqual(null).get();
			if(null==config)
				config=initConfig();
			KSessionUtil.setConfig(config);
		}
		return config;
	}

	

	@Override
	public Config initConfig() {	
		Config config=new Config();
		try {
			config.XMPPDomain="www.ulyncthings.com";
			config.XMPPHost="www.ulyncthings.com";
			config.setApiUrl("http://imapi.youjob.co/");
			config.setDownloadAvatarUrl("http://139.224.80.158:8081/");
			config.setDownloadUrl("http://139.224.80.158:8181/");
			config.setUploadUrl("http://139.224.80.158:8080/");
			config.setLiveUrl("");
			config.setFreeswitch("120.24.211.24");
			config.setMeetingHost("120.24.211.24");
			config.setShareUrl("");
			config.setSoftUrl("");
			config.setHelpUrl("");
			config.setVideoLen("20");
			config.setAudioLen("20");
			dsForRW.save(config);
				
				userManager.addUser(10000,"10000");
				userManager.addUser(10005,"10005");
				KXMPPServiceImpl.getInstance().register("10005", DigestUtils.md5Hex("10005"));
				KXMPPServiceImpl.getInstance().register("10000", DigestUtils.md5Hex("10000"));
				return config;
			} catch (Exception e) {
				e.printStackTrace();
				return null==config?null:config;
			}
	}

	
	@Override
	public void setConfig(Config config) {
		/*DBCollection dbColl = dsForRW.getDB().getCollection("config");
		DBObject q = new BasicDBObject();
		// q.put("_id", new ObjectId("55b20f2bc6054581a0e3d7e9"));
		DBObject o = new BasicDBObject();
		o.put("$set", dbObj);*/
		Config dest=getConfig();
		BeanUtils.copyProperties(config,dest);
		dsForRW.save(dest);
		KSessionUtil.setConfig(dest);
		
		
	}
	
	private void initDB(){
		// return (BasicDBObject) dsForRW
					// .getDB()
					// .getCollection("config")
					// .findOne(
					// new BasicDBObject("_id", new ObjectId(
					// "55b20f2bc6054581a0e3d7e9")));

					// for (String name : dsForRW.getDB().getCollectionNames()) {
					// if (name.contains("system.indexes"))
					// continue;
					// System.out.println("RESET COLLECTION：" + name);
					// DBCollection dbColl = dsForRW.getDB().getCollection(name);
					// dbColl.update(new BasicDBObject(), new BasicDBObject("$set",
					// new BasicDBObject("className", "")), false, true);
					// dbColl.update(new BasicDBObject(), new BasicDBObject("$unset",
					// new BasicDBObject("className", "")), false, true);
					// }
					//
					// for (String name : dsForTigase.getDB().getCollectionNames()) {
					// if (name.contains("system.indexes"))
					// continue;
					// System.out.println("RESET COLLECTION：" + name);
					// DBCollection dbColl = dsForTigase.getDB().getCollection(name);
					// dbColl.update(new BasicDBObject(), new BasicDBObject("$set",
					// new BasicDBObject("className", "")), false, true);
					// dbColl.update(new BasicDBObject(), new BasicDBObject("$unset",
					// new BasicDBObject("className", "")), false, true);
					// }
		
		
		
		
		
		/*Config obj = dbColl.findOne();
		if (null == obj) {
			
			BasicDBObject dbObj = new BasicDBObject();
			// dbObj.put("_id", new ObjectId("55b20f2bc6054581a0e3d7e9"));
			dbObj.put("XMPPDomain", "www.shiku.co");
			dbObj.put("XMPPHost", "www.shiku.co");
		
			dbObj.put("apiUrl", "http://imapi.youjob.co/");
			dbObj.put("downloadAvatarUrl", "http://file.shiku.co/");
			dbObj.put("downloadUrl", "http://file.shiku.co/");
			dbObj.put("uploadUrl", "http://upload.shiku.co/");
			
			dbObj.put("freeswitch", "120.24.211.24");
			dbObj.put("meetingHost", "120.24.211.24");
			
			dbObj.put("shareUrl", "");
			dbObj.put("softUrl", "");
			dbObj.put("helpUrl", "http,//www.youjob.co/wap/help");
			dbObj.put("videoLen", "");
			dbObj.put("audioLen", "");
			
			
			  DBObject versionInf = BasicDBObjectBuilder.start("disableVersion", "").add("version", "")
					.add("versionRemark", "").add("message", "").get();
			dbObj.put("ftpHost", "");
			dbObj.put("ftpPassword", "");
			dbObj.put("ftpUsername", "");
			dbObj.put("android", versionInf);
			dbObj.put("ios", versionInf);
			dbObj.put("buyUrl", "");
			dbObj.put("money",
					BasicDBObjectBuilder.start("isCanChange", 0).add("Login", 0).add("Share", 0).add("Intro", 0).get());
			dbObj.put("resumeBaseUrl", "http,//www.youjob.co/resume/wap");
			dbObj.put("aboutUrl", "");
			dbObj.put("website", "http,//www.shiku.co/");
			
			
			
			
			

			dbColl.save(dbObj);

			try {
				userManager.addUser(10000,"10000");
				userManager.addUser(10005,"10005");
				KXMPPServiceImpl.getInstance().register("10005", DigestUtils.md5Hex("10005"));
				KXMPPServiceImpl.getInstance().register("10000", DigestUtils.md5Hex("10000"));
				return dbColl.findOne();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else return  obj;*/
	}


}
