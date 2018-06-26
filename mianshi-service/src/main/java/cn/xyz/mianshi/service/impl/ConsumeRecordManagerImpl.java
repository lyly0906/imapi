package cn.xyz.mianshi.service.impl;


import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.repository.mongo.ConsumeRecordRepositoryImpl;

@Service
public class ConsumeRecordManagerImpl extends MongoRepository<ConsumeRecord, ObjectId>{
	
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "morphia")
	private Morphia morphia;
	@Autowired
	private UserManagerImpl userManager;
	@Autowired
	ConsumeRecordRepositoryImpl repository;
	
	public void saveConsumeRecord(ConsumeRecord entity){
		if(null==entity.getId())
			save(entity);
		else  update(entity.getId(), entity);
	}
	public ConsumeRecord getConsumeRecordByNo(String tradeNo){
		Query<ConsumeRecord> q=repository.createQuery();
		if(!StringUtil.isEmpty(tradeNo))
			q.filter("tradeNo", tradeNo);
		return q.get();
	}
	public Object reChargeList(Integer userId ,int pageIndex,int pageSize){
		Query<ConsumeRecord> q=repository.createQuery();
		q.filter("type", KConstants.MOENY_ADD);
		if(0!=userId)
			q.filter("userId", userId);
		List<ConsumeRecord> pageData=q.offset(pageIndex*pageSize).limit(pageSize).asList();
		long total=q.countAll();
		return new PageVO(pageData, total,pageIndex, pageSize);
	}
	public Object consumeRecordList(Integer userId,int pageIndex,int pageSize){
		Query<ConsumeRecord> q=repository.createQuery();
		if(0!=userId)
			q.filter("userId", userId);
			q.filter("status", KConstants.OrderStatus.END);
		List<ConsumeRecord> pageData=q.order("-time").offset(pageIndex*pageSize).limit(pageSize).asList();
		long total=q.countAll();
		return new PageVO(pageData, total,pageIndex, pageSize);
	}
	
	
}
