package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "employee", noClassnameStored = true)
@Indexes(value = {@Index(fields = {@Field("userId"),@Field("departmentId"),@Field("companyId"),@Field("role") })  })
public class Employee {
	
	private @Id ObjectId id; //员工id
	private @Indexed int userId; //用户id,用于和用户表关联
	private @Indexed ObjectId departmentId;  //部门Id,表示员工所属部门
	private @Indexed ObjectId companyId; //公司id，表示员工所属公司
	private @Indexed int role; //员工角色：0：普通员工     1：部门管理者    2：管理员    3：公司创建者(超管)
	private String position = "员工";  //职位（头衔），如：经理、总监等
	
	private @NotSaved String nickname;  //用户昵称，和用户表一致
	
	
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public ObjectId getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(ObjectId departmentId) {
		this.departmentId = departmentId;
	}
	public ObjectId getCompanyId() {
		return companyId;
	}
	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
	
}
