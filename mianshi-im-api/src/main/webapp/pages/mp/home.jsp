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
<title>首页</title>
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
.t_td {
	text-align: center;
	background-color: #73afd9;
	font-size: 18px;
	font-weight: bold;
	color: white;
}

.t_td_g {
	text-align: center;
	background-color: #58c88d;
	font-size: 18px;
	font-weight: bold;
	color: white;
}
</style>
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<div class="container"
		style="padding-left: 0px; padding-right: 0px; margin-top: 20px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<table width="100%" height="150" style="margin-bottom: 20px;">
				<tr>
					<td width="33%" class="t_td"><a href="/mp/msgs">${msgCount}<br />新消息
					</a></td>
					<td width="1%"></td>
					<td width="32%" class="t_td"><a href="/mp/fans">${fansCount}<br />新增用户
					</a></td>
					<td width="1%"></td>
					<td width="33%" class="t_td_g"><a href="/mp/fans">${userCount}<br />总用户数
					</a></td>
				</tr>
			</table>
			<div class="backgrid-container">
				<table class="backgrid">
					<thead>
						<tr>
							<th>系统公告</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>暂无公告</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
