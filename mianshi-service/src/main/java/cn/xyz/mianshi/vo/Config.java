package cn.xyz.mianshi.vo;


import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.alibaba.fastjson.JSON;

@Entity(value="config",noClassnameStored=true)
public class Config {
	/**
	 * 
	 */
	private @Id long id=10000;
	public String XMPPDomain;
	public String XMPPHost;
	
	private String apiUrl;
	private String downloadAvatarUrl;
	private String downloadUrl;
	private String uploadUrl;
	private String liveUrl;
	private String freeswitch;
	private String meetingHost;
	
	private int displayRedPacket=1;
	
	private String helpUrl;
	private String videoLen;
	private String audioLen;
	private String shareUrl;
	private String softUrl;
	
	
	private int distance;
	
	
	//以下为版本更新的字段
	private int androidVersion;  //Android 版本号
	private int iosVersion;    //ios版本号
	
	private String androidAppUrl;  //Android App的下载地址
	private String iosAppUrl;    // IOS App 的下载地址
	
	private String androidExplain; //Android 说明
	private String iosExplain;   // ios 说明
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getXMPPDomain() {
		return XMPPDomain;
	}
	public void setXMPPDomain(String xMPPDomain) {
		XMPPDomain = xMPPDomain;
	}
	public String getXMPPHost() {
		return XMPPHost;
	}
	public void setXMPPHost(String xMPPHost) {
		XMPPHost = xMPPHost;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getDownloadAvatarUrl() {
		return downloadAvatarUrl;
	}
	public void setDownloadAvatarUrl(String downloadAvatarUrl) {
		this.downloadAvatarUrl = downloadAvatarUrl;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getUploadUrl() {
		return uploadUrl;
	}
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	public String getFreeswitch() {
		return freeswitch;
	}
	public void setFreeswitch(String freeswitch) {
		this.freeswitch = freeswitch;
	}
	public String getMeetingHost() {
		return meetingHost;
	}
	public void setMeetingHost(String meetingHost) {
		this.meetingHost = meetingHost;
	}
	public String getHelpUrl() {
		return helpUrl;
	}
	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}
	public String getVideoLen() {
		return videoLen;
	}
	public void setVideoLen(String videoLen) {
		this.videoLen = videoLen;
	}
	public String getAudioLen() {
		return audioLen;
	}
	public void setAudioLen(String audioLen) {
		this.audioLen = audioLen;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getSoftUrl() {
		return softUrl;
	}
	public void setSoftUrl(String softUrl) {
		this.softUrl = softUrl;
	}
	public int getAndroidVersion() {
		return androidVersion;
	}
	public void setAndroidVersion(int androidVersion) {
		this.androidVersion = androidVersion;
	}
	public int getIosVersion() {
		return iosVersion;
	}
	public void setIosVersion(int iosVersion) {
		this.iosVersion = iosVersion;
	}
	public String getAndroidAppUrl() {
		return androidAppUrl;
	}
	public void setAndroidAppUrl(String androidAppUrl) {
		this.androidAppUrl = androidAppUrl;
	}
	public String getIosAppUrl() {
		return iosAppUrl;
	}
	public void setIosAppUrl(String iosAppUrl) {
		this.iosAppUrl = iosAppUrl;
	}
	public String getAndroidExplain() {
		return androidExplain;
	}
	public void setAndroidExplain(String androidExplain) {
		this.androidExplain = androidExplain;
	}
	public String getIosExplain() {
		return iosExplain;
	}
	public void setIosExplain(String iosExplain) {
		this.iosExplain = iosExplain;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	public String getLiveUrl() {
		return liveUrl;
	}
	public void setLiveUrl(String liveUrl) {
		this.liveUrl = liveUrl;
	}
	public int getDisplayRedPacket() {
		return displayRedPacket;
	}
	public void setDisplayRedPacket(int displayRedPacket) {
		this.displayRedPacket = displayRedPacket;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	


}
