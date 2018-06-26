package cn.xyz.mianshi.vo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.JSONUtil;
import cn.xyz.mianshi.example.AddMsgParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(value = "s_msg", noClassnameStored = true)
@Indexes(@Index("cityId,flag,userId,visible,body.title"))
public class Msg {

	private Body body;// 消息内容
	private int cityId;// 消息所属城市Id
	private String cityName;// 消息所属城市名
	private Count count;// 计数对象
	private Integer flag;// 标记（ 1=求职消息、2=招聘消息、3=普通消息）
	private Double latitude;// 纬度
	private String location;
	private Double longitude;// 经度
	private String model;// 发送消息手机型号（如：iPhone 5）
	private @Id ObjectId msgId;// 消息Id
	private String nickname;// 昵称
	private Long time;// 发消息时间
	private Integer userId;// 发消息用户Id
	private Integer visible;// 是否可见（0=不可见、1=朋友可见、2=粉丝可见、3=广场）

	private @Reference List<Comment> comments;// 评论列表
	private @Reference List<Givegift> gifts;// 礼物列表
	private @Reference List<Praise> praises;// 赞列表
	private @Transient int isPraise;// 请求接口人是否赞过本条消息

	public Body getBody() {
		return body;
	}

	public int getCityId() {
		return cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public Count getCount() {
		return count;
	}

	public Integer getFlag() {
		return flag;
	}

	public Double getLatitude() {
		return latitude;
	}

	public String getLocation() {
		return location;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getModel() {
		return model;
	}

	public ObjectId getMsgId() {
		return msgId;
	}

	public String getNickname() {
		return nickname;
	}

	public Long getTime() {
		return time;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getVisible() {
		return visible;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setCount(Count count) {
		this.count = count;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setMsgId(ObjectId msgId) {
		this.msgId = msgId;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setVisible(Integer visible) {
		this.visible = visible;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Givegift> getGifts() {
		return gifts;
	}

	public void setGifts(List<Givegift> gifts) {
		this.gifts = gifts;
	}

	public List<Praise> getPraises() {
		return praises;
	}

	public void setPraises(List<Praise> praises) {
		this.praises = praises;
	}

	public int getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}
	public static class Body {
		private String address;// 发送消息地址
		private List<Resource> audios;// 音频列表
		private List<Resource> images;// 图片列表
		private String remark;// 备注
		private String text;// 文本
		private long time;// 发送时间
		private String title;// 消息标题
		private int type;// 消息类型
		private List<Resource> videos;// 视频列表

		public String getAddress() {
			return address;
		}

		public List<Resource> getAudios() {
			return audios;
		}

		public List<Resource> getImages() {
			return images;
		}

		public String getRemark() {
			return remark;
		}

		public String getText() {
			return text;
		}

		public long getTime() {
			return time;
		}

		public String getTitle() {
			return title;
		}

		public int getType() {
			return type;
		}

		public List<Resource> getVideos() {
			return videos;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setAudios(List<Resource> audios) {
			this.audios = audios;
		}

		public void setImages(List<Resource> images) {
			this.images = images;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setVideos(List<Resource> videos) {
			this.videos = videos;
		}

	}

	public static class Count {
		private long collect;// 收藏数
		private long comment;// 评论数
		private long forward;// 转发数（APP内）
		private long money;// 金币数
		private long play;// 播放数
		private long praise;// 被赞数
		private long share;// 分享数（APP外）
		private long total;// 参考数

		public long getCollect() {
			return collect;
		}

		public long getComment() {
			return comment;
		}

		public long getForward() {
			return forward;
		}

		public long getMoney() {
			return money;
		}

		public long getPlay() {
			return play;
		}

		public long getPraise() {
			return praise;
		}

		public long getShare() {
			return share;
		}

		public long getTotal() {
			return total;
		}

		public void setCollect(long collect) {
			this.collect = collect;
		}

		public void setComment(long comment) {
			this.comment = comment;
		}

		public void setForward(long forward) {
			this.forward = forward;
		}

		public void setMoney(long money) {
			this.money = money;
		}

		public void setPlay(long play) {
			this.play = play;
		}

		public void setPraise(long praise) {
			this.praise = praise;
		}

		public void setShare(long share) {
			this.share = share;
		}

		public void setTotal(long total) {
			this.total = total;
		}
	}

	public static enum Op {
		/** 收藏 */
		Collect("count.collect"),
		/** 评论 */
		Comment("count.comment"),
		/** 转发 */
		Forwarding("count.forward"),
		/** 送礼物 */
		Gift("count.gift"),
		/** 播放音频或视频 */
		Play("count.play"),
		/** 赞 */
		Praise("count.praise"),
		/** 分享 */
		Share("count.share");

		private String key;

		private Op(String key) {
			this.key = key;
		}

		/**
		 * count.${key}
		 * 
		 * @return
		 */
		public String getKey() {
			return key;
		}
	}

	public static class Resource {
		private long length;// 播放时长

		@JsonProperty(value = "oUrl")
		@JSONField(name = "oUrl")
		private String oUrl;// 原始URL

		@JsonProperty(value = "tUrl")
		@JSONField(name = "tUrl")
		private String tUrl;// 缩略URL

		private long size;// 文件大小

		public long getLength() {
			return length;
		}

		public String getOUrl() {
			return oUrl;
		}

		public long getSize() {
			return size;
		}

		public String getTUrl() {
			return tUrl;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public void setOUrl(String oUrl) {
			this.oUrl = oUrl;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public void setTUrl(String tUrl) {
			this.tUrl = tUrl;
		}

	}

	public static Msg build(User user, AddMsgParam param) {
		Body body = new Body();
		body.title = param.getTitle();// 标题
		body.text = param.getText();// 文字内容
		body.type = param.getType();// 1=文字消息、2=图文消息、3=语音消息、4=视频消息
		// body.images = com.mongodb.util.JSON.parse(param.getImages());
		// body.audios = com.mongodb.util.JSON.parse(param.getAudios());
		// body.videos = com.mongodb.util.JSON.parse(param.getVideos());
		body.images = JSON.parseArray(param.getImages(), Resource.class);
		body.audios = JSON.parseArray(param.getAudios(), Resource.class);
		body.videos = JSON.parseArray(param.getVideos(), Resource.class);
		body.time = param.getTime();// 时间
		body.address = param.getAddress();// 地址
		body.remark = param.getRemark();// 备注

		Count count = new Count();
		count.play = 0;// 播放数
		count.forward = 0;// 转发数（APP内）
		count.share = 0;// 分享数（APP外）
		count.collect = 0;// 收藏数
		count.praise = 0;// 被赞数
		count.comment = 0;// 评论数
		count.money = 0;// 金币数
		count.total = 0;// 参考数

		Msg entity = new Msg();
		entity.msgId = ObjectId.get();
		entity.userId = user.getUserId();
		entity.nickname = user.getNickname();
		entity.flag = param.getFlag();// 1=求职消息、2=招聘消息、3=普通消息
		entity.visible = param.getVisible();// 0=不可见、1=朋友可见、2=粉丝可见、3=广场
		entity.body = body;
		entity.count = count;
		entity.time = DateUtil.currentTimeSeconds();
		entity.model = param.getModel();
		entity.cityId = param.getCityId();
		entity.cityName = param.getCityName();
		entity.latitude = param.getLatitude();
		entity.longitude = param.getLongitude();
		entity.location = param.getLocation();

		return entity;
	}

	

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
