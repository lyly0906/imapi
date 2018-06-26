<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>群发消息</title>
<link
	href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="/pages/css/backgrid.css" rel="stylesheet" />
<link href="/pages/css/css.css" rel="stylesheet" />
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<!-- <script src="http://v3.bootcss.com/assets/js/ie-emulation-modes-warning.js"></script> -->
<!-- <script src="/js/bootstrap/assets/js/vendor/holder.min.js"></script> -->
<!-- <script src="/js/bootstrap/assets/js/ie10-viewport-bug-workaround.js"></script> -->
<!--[if lt IE 9]>
<script src="http://v3.bootcss.com/assets/js/ie8-responsive-file-warning.js"></script>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<style type="text/css">
</style>

</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<%-- <jsp:include page="left.jsp"></jsp:include> --%>
	<div class="container"
		style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<form class="form-inline" action="/mp/menu/saveupdate" method="post" enctype="application/x-www-form-urlencoded">
				<select id="parentId" name="parentId" data-toggle="select"
					class="selectpicker form-control input-sm">
					<option value="0">一级菜单</option>
					<c:forEach var="o" items="${menuList}">
						<option value="${o.id}">二级菜单 - ${o.name}</option>
					</c:forEach>
				</select>
				<p>菜单名</p>
				<input id="id" name="id" type="text" class="form-control input-sm" value="${menu.id}" required="required" style="display: none"/>
				<input id="name" name="name" type="text" class="form-control input-sm" value="${menu.name}" required="required" />
				
				<p>排序</p>
				<input id="index" name="index" type="text" class="form-control input-sm" value="${menu.index}" required="required" />
				<p>访问地址</p>
				<input id="url" name="url" type="text" class="form-control input-sm" value="${menu.url}" style="width: 300px;" required="required" />
				<p>标识</p>
				<input id="menuId" name="menuId" type="text" class="form-control input-sm" value="${menu.menuId}"  />
				<button type="submit" class="btn btn-default btn-sm">提交</button>
			</form>
			
		</div>
	</div>
</body>
</html>
