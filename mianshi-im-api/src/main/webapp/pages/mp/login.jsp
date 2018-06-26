<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>酷聊公众号演示</title>
<link href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet" />
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
<body style="background-color: #F3F6F7;">
	<form name="/mp/login" method="post">
		<div class="container">
			<div class="form-signin">
				<h2 class="form-signin-heading">登录</h2>
				<input name="username" type="text" class="form-control" placeholder="帐号" required autofocus> <input name="password" type="password"
					class="form-control" placeholder="密码" required>
				<div class="checkbox">
					<!-- 					<table width=100%> -->
					<!-- 						<tr> -->
					<!-- 							<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;<label> <input type="checkbox" value="remember-me" checked="checked" -->
					<!-- 									disabled="disabled"> 记住登录 -->
					<!-- 							</label></td> -->
					<!-- 							<td align="right"><a href="register.html">注册用户</a>&nbsp;&nbsp;&nbsp;&nbsp;</td> -->
					<!-- 					</table> -->
					&nbsp;${tips}
				</div>
				<button class="btn btn-lg btn-primary btn-block">登录</button>
			</div>
		</div>
	</form>
</body>
</html>