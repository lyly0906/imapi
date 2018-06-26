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
<title>自定义菜单</title>
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
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<div class="container"
		style="padding-left: 0px; padding-right: 0px; margin-top: 20px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<table style="margin-bottom: 10px;">
				<tr>
					<td>
						<form class="form-inline" action="/mp/menu/save" method="post"
							enctype="application/x-www-form-urlencoded">
							<select id="parentId" name="parentId" data-toggle="select"
								class="selectpicker form-control input-sm">
								<option value="0">一级菜单</option>
								<c:forEach var="o" items="${menuList}">
									<option value="${o.id}">二级菜单 - ${o.name}</option>
								</c:forEach>
							</select> <input id="name" name="name" type="text"
								class="form-control input-sm" placeholder="菜单名"
								required="required" />
								<input id="index" name="index" type="text"
								class="form-control input-sm" placeholder="排序"
								required="required" />
								 <input id="url" name="url" type="text"
								class="form-control input-sm" placeholder="访问地址"
								style="width: 300px;" required="required" />
								<input id="menuId" name="menuId" type="text"
								class="form-control input-sm" placeholder="标识"
								/>
							<button type="submit" class="btn btn-default btn-sm">添加菜单</button>
						</form>
					</td>
			</table>
			<div class="backgrid-container">
				<table class="backgrid">
					<thead>
						<tr>
							<th width="50"></th>
							<th width="150">菜单Id</th>
							<th width="150">菜单名</th>
							<th width="10">排序</th>
							<th width="150">子菜单名</th>
							<th>访问地址</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="o" items="${menuList}">
							<tr>
								<td><a href="/mp/menu/delete?id=${o.id}">删除</a>|<a href="/mp/menu/update?id=${o.id}">修改</a></td>
								<td>${o.id}</td>
								<c:if test="${0 == o.parentId}">
									<td>${o.name}</td>
									<td>${o.index}</td>
									<td></td>
								</c:if>
								<c:if test="${0 != o.parentId}">
									<td>${o.index}</td>
									<td></td>
									<td>${o.name}</td>
								</c:if>
								<td>${o.url}</td>
							</tr>
							
							<c:forEach var="o2" items="${o.menuList}">
								<tr>
									<td><a href="/mp/menu/delete?id=${o2.id}">删除</a>|<a href="/mp/menu/update?id=${o2.id}">修改</a></td>
									<td>${o2.id}</td>
									<td></td>
									<td>${o2.index}</td>
									<td>${o2.name}</td>
									
									<td>${o2.url}</td>
								</tr>
							</c:forEach>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
