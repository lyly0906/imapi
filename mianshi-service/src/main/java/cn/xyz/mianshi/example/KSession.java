package cn.xyz.mianshi.example;

import com.alibaba.fastjson.JSON;

public class KSession {
	private String telephone;
	private long userId;
	private int deviceId;
	private String channelId;
	private String language="zh";

	public KSession() {
		super();
	}

	public KSession(String telephone, long userId,String language) {
		super();
		this.telephone = telephone;
		this.userId = userId;
		this.language=language;
	}
	public KSession(String telephone, long userId,int deviceId, String channelId,String language) {
		super();
		this.telephone = telephone;
		this.userId = userId;
		this.channelId=channelId;
		this.language=language;
	}

	public String getTelephone() {
		return telephone;
	}

	public long getUserId() {
		return userId;
	}


	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}


	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
