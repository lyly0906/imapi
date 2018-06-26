<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge"> -->
<!-- <meta name="viewport" content="width=device-width, initial-scale=1"> -->
<!-- <meta name="description" content=""> -->
<!-- <meta name="author" content=""> -->
<title>登录·酷聊管理后台</title>

<link href="/pages/css/css.css" rel="stylesheet">

<link  href="/pages/css/bootstrap.min.css"
	rel="stylesheet">
<script src="/pages/js/jquery-1.11.3.min.js"></script>
<script src="/pages/js/bootstrap.min.js"></script>
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</head>
<body style="background-color: #F3F6F7;">
	<form name="/admin/doLogin" method="post">
		<div class="container">
			<div class="form-signin">
				<h2 class="form-signin-heading">登录</h2>
				<input name="telephone" type="text" class="form-control" placeholder="帐号" required autofocus>
				 <input name="password" type="password" class="form-control" placeholder="密码" required>
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