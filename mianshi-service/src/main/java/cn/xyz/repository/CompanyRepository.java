package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.CompanyVO;

/**
 * 公司组织架构功能相关的数据操纵接口
 * @author hsg
 *
 */
public interface CompanyRepository {
	
	//创建公司
	CompanyVO addCompany(String companyName, int createUserId, ObjectId rootDpartId, String outRealId);
	
	//根据创建者Id查找公司
	CompanyVO findCompanyByCreaterUserId(int createUserId);
	
	//修改公司信息
	CompanyVO modifyCompany(CompanyVO company);
	
	//通过公司名称的关键字模糊查找公司
	List<CompanyVO> findCompanyByName(String keyworld);

	//根据公司id查找公司
	CompanyVO findById(ObjectId companyId);

	//根据公司outRelaId查找公司
	List<CompanyVO> findByOutRelaId(String companyId);

	//获得所有公司
	List<CompanyVO> companyList(int pageSize, int pageIndex);

	//根据公司名称查找公司，精准查找
	CompanyVO findOneByName(String companyName);
	
	//返回某个特定状态值的公司
	List<CompanyVO> findByType(int type);
}
