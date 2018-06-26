package cn.xyz.mianshi.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.CompanyExample;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.DepartmentVO;
import cn.xyz.mianshi.vo.Employee;
import cn.xyz.mianshi.vo.Member;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.User;

public interface CompanyManager {
	
	//创建公司
	CompanyVO createCompany(String companyName, int createUserId, String outRelaId);
	
	//根据userId 反向查找公司
	List<CompanyVO> findCompanyByUserId(int userId);
	
	//根据id查找公司
	CompanyVO getCompany(ObjectId companyId);

	//根据outRelaId查找公司
	List<CompanyVO> getCompanyOut(String outRelaId);
	
	//设置管理员
	void setManager(ObjectId companyId, List<Integer> managerId);
	
	//管理员列表
	List<Employee> managerList(ObjectId companyId);
	
	//修改公司信息
	CompanyVO modifyCompanyInfo(CompanyVO company);
	
	
	//通过关键字查找公司
	List<CompanyVO> findCompanyByKeyworld(String keyworld);
	
	//删除公司(即隐藏公司,不真正删除)
	void deleteCompany(ObjectId companyId,int userId);
	
	//公司列表
	List<CompanyVO> companyList(int pageSize, int pageIndex);
	
	
	
	/**部门相关*/
	
	//创建部门
	DepartmentVO createDepartment(ObjectId companyId,ObjectId parentId,String departName,int createUserId);
	
	//修改部门信息
	DepartmentVO modifyDepartmentInfo(DepartmentVO department);
	
	//删除部门
	void deleteDepartment(ObjectId departmentId);
	
	//部门列表（包括员工数据）
	List<DepartmentVO> departmentList(ObjectId companyId);

	//部门列表（包括员工数据）
	List<DepartmentVO> departmentLists(ObjectId companyId);
	
	//获取部门详情
	DepartmentVO getDepartmentVO (ObjectId departmentId);
		
	
	/**员工相关**/
	
	//添加员工(支持多个)
	List<Employee> addEmployee (ObjectId companyId, ObjectId departmentId, List<Integer> userId, int role);
	
	//删除员工
	void deleteEmployee(List<Integer> userIds, ObjectId departmentId);
	
	//更改员工信息
	Employee changeEmployeeInfo(Employee employee);
	
	//员工列表(公司的所有员工)
	List<Employee> employeeList (ObjectId companyId);
	
	//部门员工列表
	List<Employee> departEmployeeList(ObjectId departmentId);
	
	
	//获取员工详情
	Employee getEmployee(ObjectId employeeId);
	
	//员工退出公司
	void empQuitCompany(ObjectId companyId, int userId);
	
	//获取公司中某位员工的角色值
	int getEmpRole(ObjectId companyId, int userId);
	
	
	
	//此方法用于将客户自动加入默认的公司便于，客户体验组织架构功能
	CompanyVO autoJoinCompany(int userId);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//-----------------分界线---------------------------//
	




}