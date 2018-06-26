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
<title>消息管理</title>
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
			<table class="backgrid">
				<thead>
					<tr>
						<th width="50"></th>
						<th width="150">粉丝Id</th>
						<th width="150">粉丝昵称</th>
						<th width="80">未读数</th>
						<th width="">最后一条消息</th>
						<th width="80"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="o" items="${msgList}">
						<tr>
							<td><img width="40" height="40"
								src="http://file.youjob.co/avatar/o/${o.sender%10000}/${o.sender}.jpg"
								onerror="this.src='http://file.youjob.co/image/ic_avatar.png'" /></td>
							<td>${o.sender}</td>
							<td>${o.nickname}</td>
							<td><a href="/mp/msg/list?toUserId=${o.sender}"
								target="_blank">${o.count}条</a></td>
							<c:if test="${empty o.body}">
								<td>""</td>
							</c:if>
							<c:if test="${not empty o.body}">
								<td>"${o.body}"</td>
							</c:if>
							<td><a href="/mp/msg/reply?toUserId=${o.sender}"
								target="_blank">发消息</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
