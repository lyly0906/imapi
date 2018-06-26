<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls"
	prefix="utils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>单聊聊天记录·酷聊管理后台</title>
<jsp:include page="css.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<table width="1000">
		<tr>
			<td width="10%">发送者</td>
			<td width="10%">时间</td>
			<td width="80%">内容</td>
		</tr>
		<tbody>
			<c:forEach varStatus="i" var="o" items="${page.pageData}">
				<tr>
					<c:if test="${o.direction == 0}">
						<td>我</td>
					</c:if>
					<c:if test="${o.direction == 1}">
						<td>${o.receiver_nickname}</td>
					</c:if>
					<td>${utils:format(o.ts,'yyyy-MM-dd')}</td>
					<td>${o.content}</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="3">共${page.total}条聊天记录</td>
			</tr>
		</tbody>
	</table>
</body>
</html>