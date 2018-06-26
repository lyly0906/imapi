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
<title>群聊聊天记录·酷聊管理后台</title>
<jsp:include page="css.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<table>
		<tr>
			<td>房间列表</td>
			<td>聊天记录</td>
		</tr>
		<tr>
			<td valign="top"><c:if test="${empty historyList}">暂未加入房间</c:if>
				<c:if test="${not empty historyList}">
					<c:forEach varStatus="i" var="o" items="${historyList}">
						<p>
							<a href="/console/groupchat_logs?room_jid_id=${o.jid}">${o.name}</a>
						</p>
					</c:forEach>
				</c:if></td>
			<td valign="top"><c:forEach varStatus="i" var="o"
					items="${page.pageData}">
					<p>${o.body}</p>
				</c:forEach></td>
		</tr>
	</table>
</body>
</html>