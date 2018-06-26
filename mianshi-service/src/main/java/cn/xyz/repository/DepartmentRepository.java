package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.DepartmentVO;

/**
 * 组织架构功能部门相关的数据操纵接口
 * @author hsg
 *
 */
public interface DepartmentRepository {
	
	//创建部门
	ObjectId addDepartment(DepartmentVO department);
	
	//修改部门信息
	DepartmentVO modifyDepartment(DepartmentVO department);
	
	//根据Id查找部门
	DepartmentVO findDepartmentById(ObjectId departmentId);
	
	//删除部门
	void deleteDepartment(ObjectId departmentId);
	
	//部门列表(公司的所有部门,支持分页)
	List<DepartmentVO> departmentList(ObjectId companyId, int pageSize, int pageIndex);
	
	//公司部门列表，封装了员工数据
	List<DepartmentVO> departmentList(ObjectId companyId);
	
	//根据id查找部门
	DepartmentVO findById(ObjectId departmentId);
	
	//根据公司id修改根部门信息
	DepartmentVO modifyRootDepartByCompId (ObjectId companyId,DepartmentVO depart);
	
	//根据部门名称，查找某个公司的部门
	DepartmentVO findOneByName(ObjectId companyId, String departmentName);
	
	//通过部门id得到公司id
	ObjectId getCompanyId(ObjectId departmentId);
	
	//查找某个公司中某个特定状态值的部门
	List<DepartmentVO> findByType(ObjectId companyId, int type);
	
	//查找某个部门的子部门
	List<DepartmentVO> findChildDepartmeny(ObjectId departmentId);
}
