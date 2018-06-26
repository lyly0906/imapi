<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="mininav">
	<table height="100%" width="1170" align="center">
		<tr>
			<td valign="center" align="left" width="50%">
				<!-- 				<a id="a_home" href="/mp/home" style="margin-right: 20px;">首页</a>  -->
				<!-- 				<a id="a_push" href="/mp/push" style="margin-right: 20px;">群发消息</a>  -->
				<!-- 				<a id="a_menu" href="/mp/menu/list" style="margin-right: 20px;">自定义菜单</a>  -->
				<!-- 				<a id="a_msgs" href="/mp/msgs" style="margin-right: 20px;">消息管理</a>  -->
				<!-- 				<a id="a_fans" href="/mp/fans">粉丝管理</a> -->
			</td>
			<td valign="center" align="right" width="50%"><c:if
					test="${empty sessionScope.MP_USER}">您尚未登录，<a
						href="/mp/login">请登录</a>
				</c:if> <c:if test="${not empty sessionScope.MP_USER}">
					欢迎你，<b>${sessionScope.MP_USER.nickname}</b>！<a href="/mp/logout"
						style="margin-left: 10px;">退出登录</a>
				</c:if></td>
		</tr>
	</table>
</div>