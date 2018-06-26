<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>

<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
                       
</script>
<div style="margin-left: auto; margin-right: auto; width: 1000px; margin-top: 10px;">
	<form action="updateUser" method="post" class="form-horizontal">
		      		 
		      		  <input type="hidden" id="userId" name="userId" value="${u.userId}"/>
		      		  
		      		   <div class="form-group">
						    <label  class="col-sm-2 control-label">用户昵称:</label>
						    <div class="col-sm-10">
						      <input name="nickname" class="form-control" value="${u.nickname}">
						    </div>
				  	</div>
				  	
					   <div class="form-group">
					    <label  class="col-sm-2 control-label">手机号码:</label>
					    <div class="col-sm-10">
					      <input name="telephone" class="form-control" value="${u.telephone}">
					    </div>
					  </div>
					   <div class="form-group">
					    <label  class="col-sm-2 control-label">密码:</label>
					    <div class="col-sm-10">
					      <input class="form-control" type="password" name="password" value="${u.password}"/>
					    </div>
					  </div>
					   <p >
							<button   class="btn btn-primary btn-lg" type="submit">保存</button>
						</p>
				        
	      		</form>
		  
</div>

<jsp:include page="bottom.jsp"></jsp:include>