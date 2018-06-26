<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge"> -->
<!-- <meta name="viewport" content="width=device-width, initial-scale=1"> -->
<!-- <meta name="description" content=""> -->
<!-- <meta name="author" content=""> -->
<title>酷聊管理后台</title>

<link href="/pages/css/css.css" rel="stylesheet">
<link href="/pages/css/backgrid.css" rel="stylesheet">

<link  href="/pages/css/bootstrap.min.css"
	rel="stylesheet">
<script src="/pages/js/jquery-1.11.3.min.js"></script>
<script src="/pages/js/bootstrap.min.js"></script>


<!-- <link  href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css"
	rel="stylesheet"> -->
<!-- <script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.4/js/bootstrap.min.js"></script> -->

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<script type="text/javascript" src="/pages/js/echarts.min.js"></script>


</head>
<body style="background-color: #F3F6F7;">
	<div class="mininav">
		<table height="100%" width="1250" align="center">
			<tr>
				<td valign="center" align="left" width="80%">
				<a id="userStatus" href="/console/userStatus">用户在线走势图</a>&nbsp;&nbsp;&nbsp;&nbsp;
				<c:if test="${sessionScope.IS_ADMIN eq 1}">
					<a id="redPacketList" href="/console/redPacketList">红包列表</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="chat_logs_all" href="/console/chat_logs_all">单聊聊天记录管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="groupchat_logs_all" href="/console/groupchat_logs_all">群组聊天记录管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="set" href="/config/set">Config表配置</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="roomList" href="/console/roomList">房间管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="userList" href="/console/userList">用户管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="qf" href="/pages/qf.jsp">消息群发</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="LiveRoom" href="/console/liveRoomList">直播管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="gift" href="/console/giftList">礼物管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="messageList" href="/console/messageList">提示信息管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="messageList" href="/console/keywordfilter">关键词管理</a>
				</c:if>
				</td>
				<td valign="center" align="right" >
					欢迎你，<b>
						<c:if
							test="${sessionScope.IS_ADMIN == 0}">${sessionScope.LOGIN_USER.nickname}</c:if>
						<c:if test="${sessionScope.IS_ADMIN eq 1}">${sessionScope.LOGIN_USER}[管理员]</c:if>
				</b> <c:if test="${not empty sessionScope.LOGIN_USER}">
						<a href="/console/logout">退出登录</a>
					</c:if>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			$(function() {
				var url = window.location.href;
				var id = url.substr(url.lastIndexOf('/') + 1);
				if (id.indexOf('.jsp') != -1)
					id = id.replace(/.jsp/g, "");
				console.log(id)
				$("#" + id).css({
					"color" : "#000",
					"font-weight" : "bold"
				});
			});
		</script>
	</div>