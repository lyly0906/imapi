package com.shiku.mianshi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Friends;
import com.alibaba.fastjson.JSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.DepartmentVO;
import cn.xyz.mianshi.vo.Employee;

/**
 * 用于组织架构功能的相关接口
 * @author hsg
 *
 */
@RestController
@RequestMapping("/org")
public class CompanyController extends AbstractController {
	
	@Resource
	private CompanyManager companyManager;
	@Autowired
	private FriendsManager friendsManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private UserManager userManager;
	//创建公司
	@RequestMapping(value = "/company/create")
	public JSONMessage createCompany(@RequestParam String companyName, @RequestParam int createUserId, @RequestParam(defaultValue = "0") String outRelaId){
		
		try {
			if(companyName != null && !"".equals(companyName) && createUserId > 0){
				 CompanyVO company = companyManager.createCompany(companyName, createUserId, outRelaId);
				 company.setDepartments(companyManager.departmentList(company.getId()) ); //将部门及员工数据封装进公司
				 Object data = company;
				return JSONMessage.success(null, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
			
		}
		return JSONMessage.failure("创建失败");
	
	}

	
	//根据userId查找是否存在其所属的公司
	@RequestMapping("/company/getByUserId")
	public JSONMessage getCompanyByUserId(@RequestParam int userId){
		List<CompanyVO> companys = companyManager.findCompanyByUserId(userId);
		if (companys == null || companys.isEmpty()){  //如果没有就加入默认的公司
			companys=new ArrayList<CompanyVO>();
			companys.add(companyManager.autoJoinCompany(userId));
		}
		for(Iterator<CompanyVO> iter = companys.iterator(); iter.hasNext(); ){   //遍历公司
			CompanyVO company = iter.next();
			company.setDepartments(companyManager.departmentList(company.getId()) );  //将部门及员工数据封装进公司
		}
		Object data = companys;
		return JSONMessage.success(null, data);
		
	}
	
	//指定管理员
	@RequestMapping("/company/setManager")
	public JSONMessage setCompanyManager(@RequestParam String companyId, @RequestParam String managerId){
		
		//以字符串的形式接收managerId，然后解析转换为int 
		List<Integer> userIds= new ArrayList<Integer>();
		char first = managerId.charAt(0); 
		char last = managerId.charAt(managerId.length() - 1); 
		if(first=='[' && last==']'){ 
			userIds = JSON.parseArray(managerId, Integer.class);
		}
		ObjectId compId = new ObjectId(companyId);
		companyManager.setManager(compId, userIds);
		return JSONMessage.success();	
	}
	
	//管理员列表
	@RequestMapping("/company/managerList")
	public JSONMessage ManagerList(@RequestParam String companyId){
		ObjectId compId = new ObjectId(companyId);
		Object data = companyManager.managerList(compId);
		return JSONMessage.success(null, data);
	}
	
	//修改公司名称、公告
	@RequestMapping("/company/modify")
	public JSONMessage modifyCompany(@RequestParam String companyId, String companyName,@RequestParam(defaultValue = "") String noticeContent){
		ObjectId compId = new ObjectId(companyId);
		CompanyVO company = new CompanyVO();
		company.setId(compId);
		if(companyName != null){
			company.setCompanyName(companyName);
		}
		if(noticeContent != null &&  !"".equals(noticeContent)){ //判断是否存在公告
			company.setNoticeContent(noticeContent);
			company.setNoticeTime(DateUtil.currentTimeSeconds());
		}
		Object data = companyManager.modifyCompanyInfo(company);
		return JSONMessage.success(null,data);
		
	}
	
	
	//查找公司:（通过公司名称的关键字查找）
//	@RequestMapping("/company/search")
//	public JSONMessage changeNotice(@RequestParam String keyworld){
//		Object data = companyManager.findCompanyByKeyworld(keyworld);
//		return JSONMessage.success(null,data);
//	}
	
	
	//删除公司(即：记录删除者id,将公司信息隐藏)
	@RequestMapping("/company/delete")
	public JSONMessage deleteCompany(@RequestParam String companyId, int userId){
		ObjectId compId = new ObjectId(companyId);
		companyManager.deleteCompany(compId, userId);
		return JSONMessage.success();
	}
	

	//创建部门
	@RequestMapping("/department/create")
	public JSONMessage createDepartment(@RequestParam String companyId, @RequestParam String parentId, @RequestParam String departName, @RequestParam int createUserId){
		ObjectId compId = new ObjectId(companyId);
		ObjectId parentID = new ObjectId();
		if(parentId.trim() != null){
			parentID = new ObjectId(parentId);
		}
		Object data = companyManager.createDepartment(compId, parentID, departName, createUserId);
		return JSONMessage.success(null,data);
	}
	
	//修改部门名称
	@RequestMapping("/department/modify")
	public JSONMessage modifyDepartment (@RequestParam String departmentId,@RequestParam  String dpartmentName){
		ObjectId departId = new ObjectId(departmentId);
		DepartmentVO department = new DepartmentVO();
		department.setId(departId);
		department.setDepartName(dpartmentName);
		Object data = companyManager.modifyDepartmentInfo(department);
		return JSONMessage.success(null,data);
	}
	
	
	//删除部门
	@RequestMapping("/department/delete")
	public JSONMessage modifyDepartment (@RequestParam String departmentId){
		ObjectId departId = new ObjectId(departmentId);
		companyManager.deleteDepartment(departId);
		return JSONMessage.success();
	}
	
	//添加员工
	@RequestMapping("/employee/add")
	public JSONMessage addEmployee (@RequestParam String userId, @RequestParam String companyId,
									@RequestParam String departmentId, @RequestParam(defaultValue = "0") int role){
		//以字符串的形式接收userId，然后解析转换为int
		List<Integer> userIds= new ArrayList<Integer>();
		char first = userId.charAt(0); 
		char last = userId.charAt(userId.length() - 1); 
		if(first=='[' && last==']'){  //用于解析web端
			userIds = JSON.parseArray(userId, Integer.class);
		}else{ //用于解析Android和IOS端
			String[] strs = userId.split(",");
			for(String str : strs){
				if(str != null && !"".equals(str)){
					userIds.add(Integer.parseInt(str));
				}
			}
		}
		ObjectId compId = new ObjectId(companyId);
		ObjectId departId = new ObjectId(departmentId);
		Object data = companyManager.addEmployee(compId, departId, userIds, role);
		return JSONMessage.success(null,data);	
	}


	//添加员工外部数据
	@RequestMapping("/employee/addout")
	public JSONMessage addEmployeeOut (@RequestParam String userId, @RequestParam String outRelaId,
									@RequestParam String departmentName, @RequestParam(defaultValue = "0") int role, @ModelAttribute Room room, @RequestParam(defaultValue = "") String text){
		//以字符串的形式接收userId，然后解析转换为int
		String companyId = "";
		String departmentId = "";
		List<Integer> userIds= new ArrayList<Integer>();
		char first = userId.charAt(0);
		char last = userId.charAt(userId.length() - 1);
		if(first=='[' && last==']'){  //用于解析web端
			userIds = JSON.parseArray(userId, Integer.class);
		}else{ //用于解析Android和IOS端
			String[] strs = userId.split(",");
			for(String str : strs){
				if(str != null && !"".equals(str)){
					userIds.add(Integer.parseInt(str));
				}
			}
		}
		List<CompanyVO> cdata = companyManager.getCompanyOut(outRelaId);
		JSONObject companys=new JSONObject();
		List<DepartmentVO> departments = new ArrayList<DepartmentVO>();

		for (CompanyVO company : cdata) {
			companyId = company.getId().toString();
			departments = companyManager.departmentList(company.getId());
		}

		for(DepartmentVO dep : departments){
			if (dep.getDepartName().equals(departmentName)) {
				departmentId = dep.getId().toString();
				break;
			}
		}

		ObjectId compId = new ObjectId(companyId);
		ObjectId departId = new ObjectId(departmentId);
		Object data = companyManager.addEmployee(compId, departId, userIds, role);
		//创建聊天室并添加该用户
		System.out.println(room.getJid());
		if(!data.equals("") && !room.getJid().equals("")){
			String Id = roomManager.getRoomList(room.getJid());
			List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON.parseArray(text, Integer.class);
			//聊天室不存在怎创建
			System.out.println(idList);
			if(Id == null){
				roomManager.add(userManager.getUser(ReqUtil.getUserId()), room, idList);
			}else{
				roomManager.join(ReqUtil.getUserId(), new ObjectId(Id), 2);
			}

			departEmpLisbtAllBefriend(departmentId);
		}
		return JSONMessage.success(null,data);
	}
	
	
	//删除员工
	@RequestMapping("/employee/delete")
	public JSONMessage addEmployee (@RequestParam String userIds, @RequestParam String departmentId){
		
		//以字符串的形式接收userId，然后解析转换为int 
		List<Integer> uIds= new ArrayList<Integer>();
		char first = userIds.charAt(0); 
		char last = userIds.charAt(userIds.length() - 1); 
		if(first=='[' && last==']'){ //用于解析web端
			uIds = JSON.parseArray(userIds, Integer.class);
		}else{ //用于解析Android和IOS端
			uIds.add(Integer.parseInt(userIds));
		}
		ObjectId departId = new ObjectId(departmentId);
		companyManager.deleteEmployee(uIds, departId);
		return JSONMessage.success();
	}
	
	//更改员工部门
	@RequestMapping("/employee/modifyDpart")
	public JSONMessage addEmployee (@RequestParam int userId, @RequestParam String companyId, @RequestParam String newDepartmentId){
		ObjectId compId = new ObjectId(companyId);
		ObjectId departId = new ObjectId(newDepartmentId);
		Employee employee = new Employee();
		employee.setCompanyId(compId);
		employee.setUserId(userId);
		employee.setDepartmentId(departId);
		Object data = companyManager.changeEmployeeInfo(employee);  //更改该员工的信息
		
		return JSONMessage.success(null,data);
	}
	
	//部门员工列表
	@RequestMapping("/departmemt/empList")
	public JSONMessage departEmpList (@RequestParam String departmentId){
		ObjectId departId = new ObjectId(departmentId);
		Object data = companyManager.departEmployeeList(departId);
		return JSONMessage.success(null,data);
	}

	//部门员工互为好友批量操作
	@RequestMapping("/departmemt/allBefriend")
	public void departEmpLisbtAllBefriend (@RequestParam String departmentId){
		ObjectId departId = new ObjectId(departmentId);
		List<Employee> uList = companyManager.departEmployeeList(departId);

		List<Integer> employees = new ArrayList<Integer>();
		for (Employee friends : uList) {
			employees.add(friends.getUserId());
		}

		for(int i=0;i<employees.size()-1;i++){
			for(int j=i+1;j<employees.size();j++){

				System.out.println(employees.get(i)+"|"+employees.get(j));
				friendsManager.addFriends(employees.get(i),employees.get(j));
				friendsManager.followUser(employees.get(i),employees.get(j));
			}
		}
	}

	//公司员工列表
	@RequestMapping("/company/employees")
	public JSONMessage companyEmpList(@RequestParam String companyId){
		ObjectId compId = new ObjectId(companyId);
		Object data = companyManager.employeeList(compId);
		return JSONMessage.success(null,data);
	}
	
	//更改员工角色
	@RequestMapping("/employee/modifyRole")
	public JSONMessage addEmployee (@RequestParam int userId, @RequestParam String companyId, @RequestParam int role){
		ObjectId compId = new ObjectId(companyId);
		Employee employee = new Employee();
		employee.setCompanyId(compId);
		employee.setUserId(userId);
		employee.setRole(role);
		Object data = companyManager.changeEmployeeInfo(employee);
		
		return JSONMessage.success(null,data);
	}
	
	//更改员工职位(头衔)
	@RequestMapping("/employee/modifyPosition")
	public JSONMessage modifyPosition (@RequestParam int userId, @RequestParam String companyId, @RequestParam String position){
		ObjectId compId = new ObjectId(companyId);
		Employee employee = new Employee();
		employee.setCompanyId(compId);
		employee.setUserId(userId);
		employee.setPosition(position);
		Object data = companyManager.changeEmployeeInfo(employee);
		return JSONMessage.success(null,data);
	}
	
	
	//公司列表
	@RequestMapping("/company/list")
	public JSONMessage companyList (@RequestParam(defaultValue = "0") int pageIndex,@RequestParam(defaultValue = "30") int pageSize){
		Object data = companyManager.companyList(pageSize, pageIndex);
		return JSONMessage.success(null, data);
	}
		
	//部门列表
	@RequestMapping("/department/list")
	public JSONMessage departmentList (@RequestParam String companyId){
		ObjectId compId = new ObjectId(companyId);
		Object data = companyManager.departmentList(compId);
		return JSONMessage.success(null,data);
	}

	//获取公司详情
	@RequestMapping("/company/get")
	public JSONMessage getCompany (@RequestParam String companyId){
		ObjectId compId = new ObjectId(companyId);
		Object data = companyManager.getCompany(compId);
		return JSONMessage.success(null,data);
	}

	//通过外部outRelaId获取公司详情
	@RequestMapping("/company/getout")
	public JSONMessage getCompanyOut (@RequestParam String outRelaId,@RequestParam(defaultValue = "") String departmentName){
		List<CompanyVO> data = companyManager.getCompanyOut(outRelaId);
		System.out.println(data);
		JSONObject companys=new JSONObject();
		List<DepartmentVO> departments = new ArrayList<DepartmentVO>();

		if(data.size() >= 1){
			for (CompanyVO company : data) {
				companys.put("companyId",company.getId());
				companys.put("companyName",company.getCompanyName());
				companys.put("rootDpartId", company.getRootDpartId());
				departments = companyManager.departmentLists(company.getId());
			}
		}else{
			companys.put("companyId","");
			companys.put("companyName","");
		}


		List<Object> list=new ArrayList<Object>();
        for(DepartmentVO dep : departments){
			JSONObject departs=new JSONObject();
			System.out.println(dep.getDepartName()+"|"+departmentName);
			if(departmentName.equals("")){
				departs.put("departmentId",dep.getId());
				departs.put("departmentName", dep.getDepartName());
				list.add(departs);
			}else {
				System.out.println(dep.getDepartName()+"|||"+departmentName);
				if (dep.getDepartName().equals(departmentName)) {
					departs.put("departmentId", dep.getId());
					departs.put("departmentName", dep.getDepartName());
					list.add(departs);
					break;
				}
			}
		}
		companys.put("departments",list);
		return JSONMessage.success(null,companys);
	}
	
	//获取员工详情
	@RequestMapping("/employee/get")
	public JSONMessage getEmployee (@RequestParam String employeeId){
		ObjectId empId = new ObjectId(employeeId);
		Object data = companyManager.getEmployee(empId);
		return JSONMessage.success(null,data);
	}
	
	//获取部门详情
	@RequestMapping("/department/get")
	public JSONMessage getDpartment(@RequestParam String departmentId){
		ObjectId departId = new ObjectId(departmentId);
		Object data = companyManager.getDepartmentVO(departId);
		return JSONMessage.success(null,data);
	}
	
	//员工退出公司
	@RequestMapping("/company/quit")
	public JSONMessage quitCompany(@RequestParam String companyId, @RequestParam int userId){
		ObjectId compId = new ObjectId(companyId);
		companyManager.empQuitCompany(compId, userId);
		return JSONMessage.success();	
	}
	
	//获取公司中某个员工角色值
	@RequestMapping("/employee/role")
	public JSONMessage getEmployRole(@RequestParam String companyId, @RequestParam int userId){
		ObjectId compId = new ObjectId(companyId);
		Object data = companyManager.getEmpRole(compId, userId);
		return JSONMessage.success(null,data);	
	}
}
