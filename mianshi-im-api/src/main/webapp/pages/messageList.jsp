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
	function getPage(pageIndex) {
		window.location.href = "/console/messageList?pageIndex="+pageIndex;
	}
	
</script>
<!--[if lt IE 9]>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<link href="/pages/admin/css/css.css" rel="stylesheet">
<link href="/pages/admin/css/backgrid.css" rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="/pages/admin/css/dw_joblist.css">
</head>
<body style="background-color: #F3F6F7;">
	<jsp:include page="top.jsp"></jsp:include>
	<div  class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<form action="/console/messageList" class="row" method="post">

			 <div class="col-lg-3">
				<input id="keyword" name="keyword" type="text" class="form-control"
					placeholder="输入 code" value="">
			</div>
			<div class="col-lg-1">
				<button class="btn btn-danger" type="submit">搜索</button>
			</div> 
			<div class="col-lg-3">
					 <a id=“messageAdd"  href="/console/messageEdit"><button class="btn btn-info" type="button">新增提示</button></a>
			</div>
		</form>
		<div class="backgrid-container" style="margin-top: 10px;">
			<table class="backgrid " style="background-color: #fff;">
				<thead>
					<tr>
						<th width="5%">Code</th>
						<th width="5%">Type</th>
						<c:forEach varStatus="i" var="lang" items="${languages}">
							<th width="10%">${lang.name}</th>
						</c:forEach>
						<th width="150">操作</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
						<tr>
							<td>${o.code}</td>
							<td>${o.type}</td>
							<c:forEach varStatus="i" var="lang" items="${languages}">
								<td>${o[lang.key]}</td>
							</c:forEach>
							<td>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<a href="/console/messageEdit?id=${o._id}">修改</a>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<a href="/console/messageDelete?id=${o._id}">删除</a>
								
								
							</td>
						</tr>
				</c:forEach>
					<tr style="height: 40px">
						<td colspan="9" align="right"><jsp:include page="pageBar.jsp"></jsp:include></td>
					</tr>
				</tbody> 
				
			</table>
		</div>
	</div>
</body>
</html>