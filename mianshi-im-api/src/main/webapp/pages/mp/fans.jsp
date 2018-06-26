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
<title>粉丝管理</title>
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
	<script type="text/javascript">
		function getPage(pageIndex) {
			window.location.href = "/mp/fans?pageIndex=" + pageIndex
					+ "&pageSize=10";
		}
	</script>
	<jsp:include page="top.jsp"></jsp:include>
	<div class="container"
		style="padding-left: 0px; padding-right: 0px; margin-top: 20px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<div class="backgrid-container">
				<table class="backgrid">
					<thead>
						<tr>
							<th width="50"></th>
							<th width="200">粉丝Id</th>
							<th width="200">粉丝昵称</th>
							<th width=""></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="o" items="${page.pageData}">
							<tr>
								<td align="center"><img width="40" height="40"
									src="http://file.youjob.co/avatar/o/${o.toUserId%10000}/${o.toUserId}.jpg"
									onerror="this.src='http://file.youjob.co/image/ic_avatar.png'" /></td>
								<td>${o.toUserId}</td>
								<td>${o.toNickname}</td>
								<td><a href="/mp/fans/delete?toUserId=${o.toUserId}">删除</a></td>
							</tr>
						</c:forEach>
						<tr>
							<td colspan="4"><jsp:include page="../pageBar.jsp"></jsp:include></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
