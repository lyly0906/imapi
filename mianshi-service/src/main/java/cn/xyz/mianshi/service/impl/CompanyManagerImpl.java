package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.example.CompanyExample;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.DepartmentVO;
import cn.xyz.mianshi.vo.Employee;
import cn.xyz.mianshi.vo.Member;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.CompanyRepository;
import cn.xyz.repository.DepartmentRepository;
import cn.xyz.repository.EmployeeRepository;
import cn.xyz.repository.UserRepository;

@Service
public class CompanyManagerImpl implements CompanyManager {

	
	@Resource
	private CompanyRepository companyRepository;
	
	@Resource
	private DepartmentRepository departmentRepository;
	
	@Resource
	private EmployeeRepository employeeRepository;
	
	@Resource
	private UserRepository userRepository;
	
		
	
	
	
	
	

	
	
	
	
	
	/**  以下为组织架构功能相关的方法  **/
	
	
	/**
	 *创建公司（原）
	
	@Override
	public CompanyVO createCompany(String companyName, int createUserId) {
		
		//检查该用户是否创建过公司
		if(null != companyRepository.findCompanyByCreaterUserId(createUserId)){
			throw new ServiceException("已创建过公司");
		}
		
		ObjectId rootDpartId = new ObjectId();
		//添加公司记录
		CompanyVO company =  companyRepository.addCompany(companyName, createUserId, rootDpartId); 
		
		//给该公司默认添加一条根部门记录
		DepartmentVO department = new DepartmentVO();
		department.setCompanyId(company.getId());
		department.setParentId(null);  //根部门的ParentId 为null
		department.setDepartName(companyName);
		department.setCreateUserId(createUserId); //根部门的创建者即公司的创建者
		department.setCreateTime(DateUtil.currentTimeSeconds());
		department.setEmpNum(1);
		department.setType(1);  //1:根部门
		
		rootDpartId = departmentRepository.addDepartment(department); //添加根部门记录
		List<ObjectId> rootList = new ArrayList<ObjectId>();
		rootList.add(rootDpartId);
		
		//给创建者添加员工记录，将其置于根部门中
		Employee employee = new Employee();
		employee.setDepartmentId(rootDpartId);
		employee.setRole(3);   //3：公司创建者(超管)
		employee.setUserId(createUserId);
		employee.setCompanyId(company.getId());
		employeeRepository.addEmployee(employee);
		
		company.setRootDpartId(rootList);
		//将根部门id存入公司记录
		companyRepository.modifyCompany(company);
		
		
		
		
		return company;
	}

	
	 */
	
	
	
	
	
	/**
	 * 创建公司 （添加演示数据版）
	 */
	@Override
	public CompanyVO createCompany(String companyName, int createUserId, String outRealId) {
		
		//检查该用户是否创建过公司
//		if(null != companyRepository.findCompanyByCreaterUserId(createUserId)){
//			throw new ServiceException("已创建过公司");
//		}
		
		//检查是否有重名的公司
		if(null != companyRepository.findOneByName(companyName)){
			throw new ServiceException("公司名称已存在");
		}
		
		ObjectId rootDpartId = new ObjectId();
		//添加公司记录
		CompanyVO company =  companyRepository.addCompany(companyName, createUserId, rootDpartId, outRealId);
		
		//给该公司默认添加一条根部门记录
		DepartmentVO department = new DepartmentVO();
		department.setCompanyId(company.getId());
		department.setParentId(null);  //根部门的ParentId 为null
		department.setDepartName(companyName);
		department.setCreateUserId(createUserId); //根部门的创建者即公司的创建者
		department.setCreateTime(DateUtil.currentTimeSeconds());
		department.setEmpNum(0);
		department.setType(1);  //1:根部门
		
		rootDpartId = departmentRepository.addDepartment(department); //添加根部门记录
		List<ObjectId> rootList = new ArrayList<ObjectId>();
		rootList.add(rootDpartId);
		
		//给该公司创建两个部门(人事部,财务部)
		DepartmentVO personDepart = new DepartmentVO();
		personDepart.setCompanyId(company.getId());
		personDepart.setParentId(rootDpartId);  //ParentId 为根部门的id
		personDepart.setDepartName("人事部");
		personDepart.setCreateUserId(createUserId); //创建者即公司的创建者
		personDepart.setCreateTime(DateUtil.currentTimeSeconds());
		personDepart.setEmpNum(1);
		ObjectId personDepartId = departmentRepository.addDepartment(personDepart); //添加部门记录
//
//		DepartmentVO financeDepart = new DepartmentVO();
//		financeDepart.setCompanyId(company.getId());
//		financeDepart.setParentId(rootDpartId);  //ParentId 为根部门的id
//		financeDepart.setDepartName("财务部");
//		financeDepart.setCreateUserId(createUserId); //创建者即公司的创建者
//		financeDepart.setCreateTime(DateUtil.currentTimeSeconds());
//		financeDepart.setEmpNum(0);
//		departmentRepository.addDepartment(financeDepart); //添加部门记录
//
//
		//给创建者添加员工记录，将其置于人事部门中
		Employee employee = new Employee();
		employee.setDepartmentId(personDepartId);
		employee.setRole(3);   //3：公司创建者(超管)
		employee.setUserId(createUserId);
		employee.setCompanyId(company.getId());
		employeeRepository.addEmployee(employee);
		
		company.setRootDpartId(rootList);
		//将根部门id存入公司记录
		companyRepository.modifyCompany(company);
		
		
		return company;
	}
	
	
	
	
	
	//根据userId 反向查找公司
	@Override
	public List<CompanyVO> findCompanyByUserId(int userId) {
		//首先查找员工相关记录
		List<Employee> employees = employeeRepository.findByUserId(userId);
		if (employees == null){
			return null;
		}
			
		List<CompanyVO> companys = new ArrayList<CompanyVO>(); //用于存放公司的集合
		//遍历员工记录
		for(Iterator<Employee> iter = employees.iterator(); iter.hasNext();){
			Employee emp = iter.next();
			ObjectId companyId = emp.getCompanyId();
			CompanyVO comp = companyRepository.findById(companyId);
			if(comp.getDeleteUserId() == 0){  //排除掉 执行删除操作，从而被隐藏起来的公司
				companys.add(comp);
			}
		}
		if(companys.isEmpty()){ //判断是否存在公司数据
			return null;
		}
		return companys;
		
	}
	
	
	
	//根据id查找公司
	@Override
	public CompanyVO getCompany(ObjectId companyId){
		return companyRepository.findById(companyId);
	}

	//根据outRelaId查找公司
	@Override
	public List<CompanyVO> getCompanyOut( String outRelaId){
		return companyRepository.findByOutRelaId(outRelaId);
	}
	
	
	//设置管理员
	/**
	 * managerId:管理员userId
	 */
	@Override
	public void setManager(ObjectId companyId, List<Integer> managerId) {
		Employee employee = new Employee();
		for(Iterator<Integer> iter = managerId.iterator();iter.hasNext(); ){
			int userId = iter.next();
			employee.setUserId(userId);
			employee.setCompanyId(companyId);
			employee.setRole(2); //2:公司管理者
			employeeRepository.modifyEmployees(employee);
		}
		
	}
	
	
	//管理员列表
	@Override
	public List<Employee> managerList(ObjectId companyId) {
		int role = 2; //2:公司管理员
		List<Employee> list = employeeRepository.findByRole(companyId, role);
		return list;
	}
	
	
	//修改公司信息
	@Override
	public CompanyVO modifyCompanyInfo(CompanyVO company) {
		//修改公司名称的同时.修改根部门名称
		if(company.getCompanyName() != null && !"".equals(company.getCompanyName()) ){
			//判断公司名称是否重复
			if(companyRepository.findOneByName(company.getCompanyName()) != null)
				throw new ServiceException("公司名称已存在");
			
			DepartmentVO department = new DepartmentVO();
			department.setDepartName(company.getCompanyName());
			departmentRepository.modifyRootDepartByCompId(company.getId(), department);  //修改根部门名称
		}
		return companyRepository.modifyCompany(company);
	}
	

	//通过关键字查找公司
	@Override
	public List<CompanyVO> findCompanyByKeyworld(String keyworld) {
		List<CompanyVO> companys = companyRepository.findCompanyByName(keyworld);
		if(companys.size()==0){
			return null;
		}
		return companys;
	}
	
	
	//删除公司(即隐藏公司,不真正删除)
	@Override
	public void deleteCompany(ObjectId companyId, int userId) {
		//只有公司创建者才能执行删除操作
		CompanyVO company = companyRepository.findById(companyId);
		if(company.getCreateUserId() != userId){ //判断是否为创建者userId
			throw new ServiceException("只有创建者才能删除");
		}
		company.setDeleteUserId(userId);
		company.setDeleteTime(DateUtil.currentTimeSeconds());
		companyRepository.modifyCompany(company);
	}
	
	
	//公司列表  
	@Override
	public List<CompanyVO> companyList(int pageSize, int pageIndex) {
		return companyRepository.companyList(pageSize, pageIndex);
	}
	
	
	
	//创建部门
	@Override
	public DepartmentVO createDepartment(ObjectId companyId, ObjectId parentId, String departName, int createUserId) {
		//检查部门名称是否重复
		if(departmentRepository.findOneByName(companyId, departName) != null)
			throw new ServiceException("部门名称重复");
		DepartmentVO department = new DepartmentVO();
		department.setCompanyId(companyId);
		department.setParentId(parentId);
		department.setEmpNum(0);
		department.setDepartName(departName);
		department.setCreateUserId(createUserId);
		department.setCreateTime(DateUtil.currentTimeSeconds());
		ObjectId departmentId = departmentRepository.addDepartment(department);
		
		return departmentRepository.findDepartmentById(departmentId);
	}

	//修改部门信息
	@Override
	public DepartmentVO modifyDepartmentInfo(DepartmentVO department) {
		if(department.getDepartName() != null && ! "".equals(department.getDepartName()) ){
			ObjectId companyId = departmentRepository.getCompanyId(department.getId()); //通过部门id得到公司id
			int departType = departmentRepository.findById(department.getId()).getType();
			if( departType == 1 || departType == 4 || departType == 5 ){ //判断是否为特殊部门    1:表示根部门  4:演示数数据中的普通部门  5: 演示数据中的让用户加入的部门 
				throw new ServiceException("没有相关权限，不能更改");
			}
			if(departmentRepository.findOneByName(companyId, department.getDepartName()) != null)  //判断名称是否重复
				throw new ServiceException("部门名称重复");
		}
		DepartmentVO depart = departmentRepository.modifyDepartment(department);
		return depart;
	}
	
	//删除部门
	@Override
	public void deleteDepartment(ObjectId departmentId) {
		//做相关的判断，特殊部门不能删除
		int departType = departmentRepository.findById(departmentId).getType();
		if(departType == 1 || departType == 4 || departType == 5){ //1:  根部门    4:演示数数据中的普通部门  5: 演示数据中的让用户加入的部门 
			throw new ServiceException("该部门不能删除");
		}
		//首先删除该部门的员工记录
		employeeRepository.delEmpByDeptId(departmentId);
		
		departmentRepository.deleteDepartment(departmentId); //删除部门记录
	}
	
	
	//部门列表（包括员工数据）
	@Override
	public List<DepartmentVO> departmentList(ObjectId companyId) {
		 
		List<DepartmentVO> departments = departmentRepository.departmentList(companyId); //查找出该公司所有的部门数据
		for(Iterator<DepartmentVO> iter = departments.iterator();iter.hasNext(); ){
			DepartmentVO department = iter.next();
			//查找该部门中的员工,并封装到department中
			List<Employee> employees = employeeRepository.departEmployeeList(department.getId());
			setUserNickname(employees); //将用户昵称封装到员工数据
			department.setEmployees(employees); //将员工数据封装进部门中
		}
		return departments;
	}

	//部门列表（包括员工数据）
	@Override
	public List<DepartmentVO> departmentLists(ObjectId companyId) {

		List<DepartmentVO> departments = departmentRepository.departmentList(companyId); //查找出该公司所有的部门数据
		/*for(Iterator<DepartmentVO> iter = departments.iterator();iter.hasNext(); ){
			DepartmentVO department = iter.next();
			//查找该部门中的员工,并封装到department中
			List<Employee> employees = employeeRepository.departEmployeeList(department.getId());
			setUserNickname(employees); //将用户昵称封装到员工数据
			department.setEmployees(employees); //将员工数据封装进部门中
		}*/
		return departments;
	}

	public List<DepartmentVO> departmentList(List<DepartmentVO> departments) {
		 
		for(Iterator<DepartmentVO> iter = departments.iterator();iter.hasNext(); ){  
			DepartmentVO department = iter.next();
			//查找该部门中的员工,并封装到department中
			List<Employee> employees = employeeRepository.departEmployeeList(department.getId());
			setUserNickname(employees); //将用户昵称封装到员工数据
			department.setEmployees(employees); //将员工数据封装进部门中
		}
		return departments;
	}
	
	
	//添加员工(支持多个) 
	@Override
	public List<Employee> addEmployee(ObjectId companyId, ObjectId departmentId, List<Integer> userId, int role) {
		//判断是否能够添加
		List<Employee> compEmps = employeeRepository.compEmployeeList(companyId); //得到公司的所有员工
		for(Iterator<Integer> uids = userId.iterator(); uids.hasNext();){  //遍历userId，和公司的所有员工进行比对
			int uid = uids.next();
			for(Iterator<Employee> iter = compEmps.iterator(); iter.hasNext(); ){
				Employee emp = iter.next();	
				if(emp.getUserId() == uid){  //判断员工是否存在
					throw new ServiceException("不能重复添加");
				}
			}
			
		}
		
		//正常添加员工
		List<Employee> employees = employeeRepository.addEmployees(userId, companyId, departmentId, role);  //存入员工记录
		//修改公司记录里的员工数目
		CompanyVO company = companyRepository.findById(companyId);
		company.setEmpNum(company.getEmpNum()+userId.size());
		companyRepository.modifyCompany(company);
		
		//修改部门记录里的员工数目
		DepartmentVO department = departmentRepository.findById(departmentId);
		department.setEmpNum(department.getEmpNum()+userId.size());
		departmentRepository.modifyDepartment(department);
		
		setUserNickname(employees);//将用户昵称封装到员工数据
		return employees;
	}
	
	
	//删除员工
	@Override
	public void deleteEmployee(List<Integer> userIds, ObjectId departmentId) {
		employeeRepository.deleteEmployee(userIds, departmentId); //删除员工记录
		//修改部门记录里的员工数目    -1
		DepartmentVO department = departmentRepository.findById(departmentId);
		department.setEmpNum(department.getEmpNum()-1);
		departmentRepository.modifyDepartment(department);
		
		//修改公司记录里的员工数目
		CompanyVO company = companyRepository.findById(department.getCompanyId());
		company.setEmpNum(company.getEmpNum()-1);
		companyRepository.modifyCompany(company);
	}
	
	//更改员工信息
	@Override
	public Employee changeEmployeeInfo(Employee employee) {
		Employee emp = employeeRepository.modifyEmployees(employee);
		String nickname = userRepository.getUser(emp.getUserId()).getNickname();
		emp.setNickname(nickname);
		return emp;
	}
	
	//员工列表(公司的所有员工)
	@Override
	public List<Employee> employeeList(ObjectId companyId) {
		List<Employee> employees = employeeRepository.compEmployeeList(companyId);
		setUserNickname(employees);//将用户昵称封装到员工数据
		return employees;
	}
	
	//部门员工列表
	public List<Employee> departEmployeeList(ObjectId departmentId){
		List<Employee> employees = employeeRepository.departEmployeeList(departmentId);
		setUserNickname(employees); //将用户昵称封装到员工数据
		return  employees;
	}


	
	//获取员工详情
	@Override
	public Employee getEmployee(ObjectId employeeId) {
		Employee emp =  employeeRepository.findById(employeeId);
		String nickname = userRepository.getUser(emp.getUserId()).getNickname();
		emp.setNickname(nickname);
		return emp;
	}

	
	//获取部门详情
	@Override
	public DepartmentVO getDepartmentVO(ObjectId departmentId) {
		DepartmentVO department = departmentRepository.findById(departmentId);
		return department;
	}

	
	
	//此方法用于获取user表中的用户昵称，并封装进员工数据中
	public void setUserNickname(List<Employee> employees){
		for(Iterator<Employee> iter = employees.iterator();iter.hasNext();){  //遍历员工数据
			Employee employee = iter.next();
			String nickname = userRepository.getUser(employee.getUserId()).getNickname();
			employee.setNickname(nickname);
		}
	}
	

	
	//此方法用于将客户自动随机加入默认的公司,便于客户体验组织架构功能
	@Override
	public CompanyVO autoJoinCompany(int userId){
		Employee employee = new Employee();
		List<CompanyVO> companys = companyRepository.findByType(5);  //查找默认加入的公司
		if(companys == null || companys.isEmpty()){
			return null;
		}
		//随机选择一个公司
		Random random = new Random();
		int comp = random.nextInt(companys.size()); 
		employee.setCompanyId(companys.get(comp).getId());  //设置 员工记录的companyId 为默认随机加入的公司的id
		
		
		//随机选择该公司下的一个部门
		List<DepartmentVO> departments = departmentRepository.findByType(companys.get(comp).getId(), 5);
		
		int dept = random.nextInt(departments.size());
		employee.setDepartmentId(departments.get(dept).getId()); //设置 员工记录的departmentId 为随机加入的部门的id
		
		employee.setUserId(userId);
		employee.setRole(0);
		ObjectId id = employeeRepository.addEmployee(employee);
		
		//将公司数据返回
		return companyRepository.findById(companys.get(comp).getId());
	}
	
	
	//员工退出公司
	@Override
	public void empQuitCompany(ObjectId companyId, int userId) {
		//判断员工身份，若为创建者直接删除公司(隐藏公司)，非创建者将该员工从公司中移除
		int employeeRole = employeeRepository.findRole(companyId, userId);//获取员工角色值
		if(employeeRole == 3){    //  3 : 表示公司的创建者
			//删除（隐藏公司）
			deleteCompany(companyId, userId);
		}else{
			//删除员工记录
			employeeRepository.delEmpByCompId(companyId, userId);
		}
		
	}


	//获取公司中某位员工的角色值
	@Override
	public int getEmpRole(ObjectId companyId, int userId) {
		return employeeRepository.findRole(companyId, userId);
	}



	
	
}