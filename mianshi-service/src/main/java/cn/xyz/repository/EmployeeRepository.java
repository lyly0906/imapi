package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.Employee;

/**
 * 组织架构功能员工相关的数据操纵接口
 * @author hsg
 *
 */
public interface EmployeeRepository {
	
	
	//添加员工（单个）
	ObjectId addEmployee(Employee employee);
	
	//添加员工（多个）
	List<Employee> addEmployees(List<Integer> userId, ObjectId companyId, ObjectId departmentId, int role);
	
	//修改员工信息
	Employee modifyEmployees(Employee employee);
	
	//根据id查找员工
	Employee findById(ObjectId employeeId);
	
	//通过userId查找员工
    List<Employee> findByUserId(int userId);
	
    //查找公司中某个角色的所有员工
    List<Employee> findByRole(ObjectId companyId,int role);
	
	//删除整个部门的员工
    void delEmpByDeptId(ObjectId departmentId);
    
    //删除员工(单个)
    void deleteEmployee(List<Integer> userIds, ObjectId departmentId);
    
    //根据公司ID查询员工(员工列表)
    List<Employee> compEmployeeList (ObjectId companyId);
    
    //根据部门ID查询员工(部门员工列表) 不分页
    List<Employee> departEmployeeList (ObjectId departmentId);
    
    //查找公司中某个员工的角色
    int findRole(ObjectId companyId, int userId);
    
    //删除员工(根据公司id）
    void delEmpByCompId(ObjectId companyId, int userId);
    
}
