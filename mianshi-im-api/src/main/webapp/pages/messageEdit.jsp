<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>酷聊管理后台</title>

<script type="text/javascript">
	
</script>
<!--[if lt IE 9]>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<link href="/pages/admin/css/css.css" rel="stylesheet">
<link href="/pages/admin/css/backgrid.css" rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="/pages/admin/css/dw_joblist.css">
<style type="text/css">
.rowdiv{
margin:25px;
white-space:nowrap;
}

</style>
</head>
<body style="background-color: #F3F6F7; text-align: center;">
	<jsp:include page="top.jsp"></jsp:include>
		<form action="/console/messageUpdate" role="form" class="form-horizontal container" method="post">
	
				<c:if test="${action=='update'}">
				  	<input type="hidden" name="_id" value="${o._id}">
				</c:if>
				
				   
				   <div class="form-group">
				    	<label  class="col-sm-2 control-label">Code</label>
					    <div class="col-sm-10">
					      <input name="code"  class="form-control" 
					      value="${o.code}">
					    </div>
				   </div>
				    
				   <div class="form-group">
				    	<label  class="col-sm-2 control-label">Type</label>
					    <div class="col-sm-10">
					      <input name="type"  class="form-control" 
					      value="${o.type}">
					    </div>
				   </div>
				   
				 <c:forEach varStatus="i" var="lang" items="${languages}">
					 <div class="form-group">
					    <label  class="col-sm-2 control-label">${lang.name}</label>
					    <div class="col-sm-10">
					      <input name="${lang.key}" class="form-control" value="${o[lang.key]}">
					    </div>
					  </div>
	           	  
				 </c:forEach>
	           	<p >
					<button  class="btn btn-primary btn-lg" type="submit">保存</button>
				</p>
		</form>
</body>
</html>