package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

//消费记录实体
@Entity(value = "ConsumeRecord", noClassnameStored = true)
public class ConsumeRecord {

	private @Id ObjectId id;
	private @Indexed String tradeNo;
	private @Indexed Integer userId; //用户Id
	private Double money;
	//private Double 
	private Double startMoney;
	private Double endMoney;
	private long time;
	private @Indexed int type; //消费类型  1：充值 2:消费
	private @Indexed ObjectId orderId; //type=2 消费时会有订单Id
	private String desc; //消费备注
	private int payType; //支付方式 1：支付宝支付  2：微信支付  //3：余额支付
	private @Indexed int status; //交易状态 0：创建  1：支付完成  2：交易完成  -1：交易关闭 
	

	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Double getStartMoney() {
		return startMoney;
	}
	public void setStartMoney(Double startMoney) {
		this.startMoney = startMoney;
	}
	public Double getEndMoney() {
		return endMoney;
	}
	public void setEndMoney(Double endMoney) {
		this.endMoney = endMoney;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ObjectId getOrderId() {
		return orderId;
	}
	public void setOrderId(ObjectId orderId) {
		this.orderId = orderId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
