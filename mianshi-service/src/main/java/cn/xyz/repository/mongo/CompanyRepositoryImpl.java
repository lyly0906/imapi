package cn.xyz.repository.mongo;





import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.DepartmentVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.CompanyRepository;

/**
 * 组织架构功能数据操纵接口的实现
 * @author hsg
 *
 */
@Service
public class CompanyRepositoryImpl extends BaseRepositoryImpl<CompanyVO, ObjectId> implements CompanyRepository {
	
	public static CompanyRepositoryImpl getInstance(){
		return new CompanyRepositoryImpl();
	}
	
	
	 //创建公司
	@Override   
	public CompanyVO addCompany(String companyName, int createUserId, ObjectId rootDpartId, String outRelaId) {
		
		CompanyVO company = new CompanyVO();
		List<ObjectId> list = new ArrayList<ObjectId>();
		list.add(rootDpartId);
		
		company.setCompanyName(companyName);
		company.setCreateUserId(createUserId);
		company.setDeleteUserId(0);
		company.setCreateTime(DateUtil.currentTimeSeconds());
		company.setRootDpartId(list);
		company.setNoticeContent("");
		company.setDeleteTime(0);
		company.setNoticeTime(0);
		company.setEmpNum(1);
		company.setOutRealId(outRelaId);
		
		//存入公司数据
		ObjectId companyId = (ObjectId) dsForRW.save(company).getId();
		company.setId(companyId);
		
		return company;
	}

	
	//根据创建者Id查找公司
	@Override
	public CompanyVO findCompanyByCreaterUserId(int createUserId) {
		//根据创建者Id查找公司，同时排除掉deleteUserId != 0 的数据(deleteUserId != 0 :表示已经删除）
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("createUserId").equal(createUserId).filter("deleteUserId  ==", 0);
		return query.get();
	}

	
	//修改公司信息
	@Override
	public CompanyVO modifyCompany(CompanyVO company) {
		ObjectId companyId = company.getId();
		if(companyId == null){
			return null;
		}
		
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("_id").equal(companyId);
		UpdateOperations<CompanyVO> ops = dsForRW.createUpdateOperations(CompanyVO.class);
		if(null != company.getCompanyName())
			ops.set("companyName", company.getCompanyName());
		if(0 != company.getCreateUserId())
			ops.set("createUserId", company.getCreateUserId());
		if(0 != company.getDeleteUserId())
			ops.set("deleteUserId", company.getDeleteUserId());
		if(null != company.getRootDpartId())
			ops.set("rootDpartId", company.getRootDpartId());
		if(0 != company.getCreateTime())
			ops.set("createTime", company.getCreateTime());
		if(null != company.getNoticeContent()){
			ops.set("noticeContent", company.getNoticeContent());
			ops.set("noticeTime", DateUtil.currentTimeSeconds());
		}
		if(0 != company.getDeleteTime())
			ops.set("deleteTime", company.getDeleteTime());
		if(0 != company.getEmpNum())
			ops.set("empNum", company.getEmpNum());
		
		CompanyVO comp = dsForRW.findAndModify(query, ops);
		
		return comp;
	}

	
	//通过公司名称的关键字模糊查找公司
	@Override
	public List<CompanyVO> findCompanyByName(String keyworld) {
		
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class);
		
		//忽略大小写进行模糊匹配
		query.criteria("companyName").containsIgnoreCase(keyworld);
		List<CompanyVO> companys = query.asList();
		
		//除去执行过删除操作,被隐藏的公司
		for (Iterator<CompanyVO> iter = companys.iterator(); iter.hasNext();) {  
			CompanyVO company = iter.next();  
            if (company.getDeleteUserId() != 0) {   //将DeleteUserId不为0的数据剔除
                iter.remove();  
            }  
        }  
		
		return companys;
	}
	
	//根据公司id查找公司
	@Override
	public CompanyVO findById(ObjectId companyId){
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("_id").equal(companyId);
		if(query == null){
			return null;
		}
		
		return query.get();
	}



	//根据外部outRelaId查找公司
	@Override
	public List<CompanyVO> findByOutRelaId(String outRelaId){
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("outRelaId").equal(outRelaId);

		List<CompanyVO> companys = query.asList();

		return companys;
	}

	
	
	//获得所有公司
	@Override
	public List<CompanyVO> companyList(int pageSize, int pageIndex) {
		//查找没有被隐藏起来的公司
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("deleteUserId").equal(0);
		List<CompanyVO> companys = query.offset(pageIndex * pageSize).limit(pageSize).asList();
		
		return companys;
	}

	
	//根据公司名称查找公司，精准查找
	@Override
	public CompanyVO findOneByName(String companyName) {
		//查找公司名称完全匹配，且没有被隐藏起来的公司
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("companyName").equal(companyName).field("deleteUserId").equal(0);
		return query.get();
	}

	
	//返回某个特定状态值的公司
	@Override
	public List<CompanyVO> findByType(int type) {
		Query<CompanyVO> query = dsForRW.createQuery(CompanyVO.class).field("type").equal(type);
		return query.asList();
	}
	

}
