package cn.xyz.repository.mongo;

import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.DepartmentVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.DepartmentRepository;
import cn.xyz.repository.MongoRepository;

/**
 * 
 * 组织架构功能部门数据操纵接口的实现
 * @author hsg
 *
 */

@Service
public class DepartmentRepositoryImpl extends MongoRepository implements DepartmentRepository{
	
	
	//创建部门,返回值为部门Id
	@Override 
	public ObjectId addDepartment(DepartmentVO department) {
		//存入数据，并获取id
		 ObjectId departmentId = (ObjectId) dsForRW.save(department).getId();
		 return departmentId;
	}
	
	
	//修改部门信息
	@Override
	public DepartmentVO modifyDepartment(DepartmentVO department) {
		
		ObjectId departmentId = department.getId();
		
		if(departmentId == null){
			return null;
		}
		
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("_id").equal(departmentId);
		UpdateOperations<DepartmentVO> ops = dsForRW.createUpdateOperations(DepartmentVO.class);
		
		if(null != department.getDepartName())
			ops.set("departName", department.getDepartName());
		if(0 <= department.getEmpNum())
			ops.set("empNum", department.getEmpNum());
		DepartmentVO depart = dsForRW.findAndModify(query, ops);
		
		
		return depart;
		
		
	}
	
	
	//根据部门Id查找部门
	@Override 
	public DepartmentVO findDepartmentById(ObjectId departmentId) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("_id").equal(departmentId);
		return query.get();
	}

	
	//删除部门
	@Override
	public void deleteDepartment(ObjectId departmentId) {
		//根据id找到部门
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("_id").equal(departmentId);
		//删除记录
		if(query != null)
		dsForRW.delete(query);
	}

	
	//部门列表(公司的所有部门，包含员工,分页)
	@Override
	public List<DepartmentVO> departmentList(ObjectId companyId, int pageSize, int pageIndex) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("companyId").equal(companyId);
		List<DepartmentVO> departments = query.offset(pageIndex * pageSize).limit(pageSize).asList();
		
		return departments;
	}

	//根据id查找部门
	@Override
	public DepartmentVO findById(ObjectId departmentId) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("_id").equal(departmentId);
		return query.get();
	}

	//公司部门列表，封装员工数据
	@Override
	public List<DepartmentVO> departmentList(ObjectId companyId) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("companyId").equal(companyId);
		return query.order("createTime").asList();  //按创建时间升序排列
	}

	
	//根据公司id修改根部门信息
	@Override
	public DepartmentVO modifyRootDepartByCompId(ObjectId companyId, DepartmentVO depart) {
		//查找根部门
		Query<DepartmentVO> dQuery = dsForRW.createQuery(DepartmentVO.class).field("companyId").equal(companyId).field("type").equal(1);  //type:1   1:根部门
		depart.setId(dQuery.get().getId());
		//更新信息
        UpdateOperations<DepartmentVO> ops = dsForRW.createUpdateOperations(DepartmentVO.class);
		if(null != depart.getDepartName())
			ops.set("departName", depart.getDepartName());
		if(0 <= depart.getEmpNum())
			ops.set("empNum", depart.getEmpNum());
		
		 dsForRW.findAndModify(dQuery, ops);
		
		return null;
	}

	
	//根据部门名称，查找某个公司的部门,精准查找
	@Override
	public DepartmentVO findOneByName(ObjectId companyId, String departName) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("companyId").equal(companyId).field("departName").equal(departName);
		return query.get();
	}

    //通过部门id得到公司id
	@Override
	public ObjectId getCompanyId(ObjectId departmentId) {
		Query<DepartmentVO> dQuery = dsForRW.createQuery(DepartmentVO.class).field("_id").equal(departmentId);
		return dQuery.get().getCompanyId();
	}

	
	//查找某个公司中某个特定状态值的部门
	@Override
	public List<DepartmentVO> findByType(ObjectId companyId, int type) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("companyId").equal(companyId).field("type").equal(type);
		return query.asList();
	}

	
	//查找某个部门的子部门
	@Override
	public List<DepartmentVO> findChildDepartmeny(ObjectId departmentId) {
		Query<DepartmentVO> query = dsForRW.createQuery(DepartmentVO.class).field("parentId").equal(departmentId);
		return query.asList();
	}

	
	
	
	
}
