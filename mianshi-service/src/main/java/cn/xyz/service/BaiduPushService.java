package cn.xyz.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import cn.xyz.commons.utils.FileUtil;
import cn.xyz.commons.utils.Md5Util;

public class BaiduPushService {

	public static class PushMessage {
		public static class Aps {
			private String alert;
			private int badge;
			private String sound;

			public Aps() {
				super();
			}

			public Aps(String alert) {
				super();
				this.alert = alert;
			}

			public String getAlert() {
				return alert;
			}

			public int getBadge() {
				return badge;
			}

			public String getSound() {
				return sound;
			}

			public void setAlert(String alert) {
				this.alert = alert;
			}

			public void setBadge(int badge) {
				this.badge = badge;
			}

			public void setSound(String sound) {
				this.sound = sound;
			}
			@Override
			public String toString() {
				return JSON.toJSONString(this);
			}
		}

		private Aps aps;
		private String description;// 内容
		private String title;// 标题

		private ObjectId jobId;// 数据：职位Id等
		private String jobName;
		private String name;
		private int opId;// 操作Id：见常量表（1=刚刚加入；2=查看职位；3=感兴趣职位；4=发布职位；5=查看简历；6=感兴趣简历）
		private int status;// 状态：0=未读；1=已读
		private long time;
		private String toName;
		private int type;//消息类型
		private long toUserId; // 用户Id（个人用户或企业用户）
		private long userId;// 用户Id（个人用户或企业用户）
		/**
		 * 
		 */
		public PushMessage() {
			// TODO Auto-generated constructor stub
		}
		public PushMessage(Long from, String nickname, Long to, String text, int messageType, long ts) {
			this.userId = from;
			this.name = "";
			this.toUserId = to;
			this.toName = "";
			this.opId = 0;
			this.jobId = null;
			this.jobName = "";
			this.time = ts;
			this.status = 0;
			String desc = "";
			switch (messageType) {
			// kWCMessageTypeText = 1,//文本
			// kWCMessageTypeImage = 2,//图片
			// kWCMessageTypeVoice = 3,//语音
			// kWCMessageTypeLocation=4, //位置
			// kWCMessageTypeGif=5,//动画
			// kWCMessageTypeVideo=6,//视频
			// kWCMessageTypeAudio=7,//音频
			// kWCMessageTypeCard=8,//名片
			// kWCMessageTypeFile=9, //文件
			// kWCMessageTypeRemind=10, //提醒
			case 1:
				desc += text;
				break;
			case 2:
				desc += "发来一个图片";
				break;
			case 3:
				desc += "发来一段语音";
				break;
			case 4:
				desc += "发来一个位置";
				break;
			case 5:
				desc += "发来一个表情";
				break;
			case 6:
				desc += "发来一段视频";
				break;
			case 7:
				desc += "发来一段语音";
				break;
			case 8:
				desc += "发来一张名片";
				break;
			case 9:
				desc += "发来一个文件";
				break;
			case 10:
				desc += "发来一个提醒";
				break;
			case 500:
				desc +="请求加为好友";
				break;
			case 501:
				desc +="已同意加好友";
				break;
			case 502:
				desc +="已拒绝加好友";
				break;
			case 907:
				desc +="已加入群";
				break;
			}

			this.title = nickname;
			this.description = desc;
			this.aps = new Aps(nickname + "："+desc);
		}
		public void bindMsg(PushMessage msg){
			
		}

		public Aps getAps() {
			return aps;
		}

		public void setAps(Aps aps) {
			this.aps = aps;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public ObjectId getJobId() {
			return jobId;
		}

		public void setJobId(ObjectId jobId) {
			this.jobId = jobId;
		}

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getOpId() {
			return opId;
		}

		public void setOpId(int opId) {
			this.opId = opId;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getToName() {
			return toName;
		}

		public void setToName(String toName) {
			this.toName = toName;
		}

		public long getToUserId() {
			return toUserId;
		}

		public void setToUserId(long toUserId) {
			this.toUserId = toUserId;
		}

		public long getUserId() {
			return userId;
		}

		public void setUserId(long userId) {
			this.userId = userId;
		}

		@Override
		public String toString() {
			return JSON.toJSONString(this);
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

	}

	// public static final String APP_ID = "6923066";
	// public static final String APP_KEY = "nrDzlZBxI7qCG5y2pbLm46P6";
	// public static final String REST_URL = "http://api.tuisong.baidu.com";
	// public static final String SECRET_KEY =
	// "HjPjfzS8WGxBeG72Y5hkvt7NpZDWfKb8";

	// EMPTY，安卓，iOS
	// public static final String[] APP_ID = new String[] { "", "7134076",
	// "7134076" };
	public static final String APPSTORE_APPID="qxt_ios";
	public static final String APPSTORE_APPKEY="YWCjFscGk7cv3RlEtaxoypzt0sipp6vw";
	public static final String APPSTORE_SECRET_KEY="Y5tsqtPYDtTHFC1NjkccEYqyLVZ9jGnh";
	public static final String[] APP_KEY = new String[] { "", "jilKjyyHCu2H6G96NvlxM94iYHtMcV3s", "KB50jpXSekiSMKA4pWN9PBvh" };
	public static final String REST_URL = "http://api.tuisong.baidu.com";
	public static final String[] SECRET_KEY = { "", "p4PBL909bh8KU3NsGIRskmjDgm4a5TX2",
			"KOazHlck323rKSNkE0ZShXb27wKzEck5" };

	private static byte[] getBytes(Map<String, Object> params) throws Exception {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			// String value = URLEncoder.encode(params.get(key).toString(),
			// "UTF-8");
			sb.append(key).append('=').append(params.get(key)).append('&');
		}
		return sb.substring(0, sb.length() - 1).getBytes("UTF-8");
	}

	private static String getSign(int deviceId, String url, String method, Map<String, Object> params,String appId)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(method).append(url);
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			sb.append(key).append('=').append(params.get(key));
		}
		String baseStr =null;
		if(APPSTORE_APPID.equals(appId))
			 sb.append(APPSTORE_SECRET_KEY);
		else
			sb.append(SECRET_KEY[deviceId]);
		baseStr=sb.toString();
		// String signStr = DigestUtils.md5Hex(URLEncoder.encode(baseStr,
		// "UTF-8").replaceAll("\\*", "%2A"));
		String signStr = Md5Util.md5Hex(URLEncoder.encode(baseStr, "UTF-8").replaceAll("\\*", "%2A"));
		return signStr;
	}

	// private static String getUserAgent() {
	// StringBuffer sb = new StringBuffer();
	// String sysName = System.getProperty("os.name");
	// String sysVersion = System.getProperty("os.version");
	// String sysArch = System.getProperty("os.arch");
	// String sysLangVersion = System.getProperty("java.version");
	// sb.append("BCCS_SDK/3.0 (");
	// if (sysName != null) {
	// sb.append(sysName).append("; ");
	// }
	// if (sysVersion != null) {
	// sb.append(sysVersion).append("; ");
	// }
	// if (sysArch != null) {
	// sb.append(sysArch);
	// }
	// sb.append(") ").append("JAVA/").append(sysLangVersion).append(" (Baidu
	// Push Server SDK V.2.0.0)");
	// return sb.toString();
	// }

	public static String getString(String spec, String method, Map<String, Object> params) throws Exception {
		URL url = new URL(spec);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod(method);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		con.setRequestProperty("User-Agent",
				"BCCS_SDK/3.0 (Windows 7; 6.1; amd64) JAVA/1.8.0_05 (Baidu Push Server SDK V.2.0.0)");
		con.connect();

		OutputStream out = con.getOutputStream();
		out.write(getBytes(params));
		out.flush();
		out.close();

		if (200 == con.getResponseCode()) {
			return FileUtil.readAll(con.getInputStream());
		} else {
			return FileUtil.readAll(con.getErrorStream());
		}
	}

	public static void pushAll(int deviceId, PushMessage msg) {
		try {
			String spec = REST_URL + "/rest/3.0/push/all";
			String method = "POST";
			Map<String, Object> params = Maps.newTreeMap();
			params.put("apikey", getAppKey(deviceId));
			params.put("timestamp", System.currentTimeMillis() / 1000);
			params.put("msg", msg.toString());
			params.put("msg_type", 1);
			// params.put("deploy_status", 1);
			params.put("sign", getSign(deviceId, spec, method, params,""));
			String s = getString(spec, method, params);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pushSingle(int deviceId, String channelId, PushMessage msg,String appId) {
		try {

			String spec = REST_URL + "/rest/3.0/push/single_device";
			String method = "POST";
			Map<String, Object> params = Maps.newTreeMap();
			if(APPSTORE_APPID.equals(appId))
				params.put("apikey",APPSTORE_APPKEY);
			else
				params.put("apikey", getAppKey(deviceId));
			params.put("timestamp", System.currentTimeMillis() / 1000);
			params.put("channel_id", channelId);
			params.put("msg_type", 1);
			 //1：开发状态；2：生产状态； 若不指定，则默认设置为生产状态。
			params.put("deploy_status",1);
			if(deviceId==2)
			params.put("msg", msg.toString());
			else
				params.put("msg", msg.toString());
			params.put("sign", getSign(deviceId, spec, method, params,appId));
            System.out.println(params);

			String s = getString(spec, method, params);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static String getAppKey(int deviceId) {
		return APP_KEY[deviceId];
	}

}
