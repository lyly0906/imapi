<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- ${page.total}条记录，每页${page.pageSize}条，共${page.pageCount}页，当前第${page.pageIndex+1}页， --%>
<%-- <c:if test="${page.pageIndex<=0}">首页&nbsp;上一页</c:if> --%>
<%-- <c:if test="${page.pageIndex>0}"> --%>
<!-- 	<a href="/console/chat_logs_all?pageIndex=0">首页</a>&nbsp;<a -->
<%-- 		href="/console/chat_logs_all?pageIndex=${page.pageIndex-1}">上一页</a> --%>
<%-- </c:if> --%>
<%-- <c:if test="${page.pageIndex<page.pageCount-1}"> --%>
<%-- 	<a href="/console/chat_logs_all?pageIndex=${page.pageIndex+1}">下一页</a>&nbsp; --%>
<%-- 				<a href="/console/chat_logs_all?pageIndex=${page.pageCount-1}">尾页</a> --%>
<%-- </c:if> --%>
<%-- <c:if test="${page.pageIndex>=page.pageCount-1}">下一页&nbsp;尾页</c:if> --%>
${page.total}条记录，每页${page.pageSize}条，共${page.pageCount}页，当前第${page.pageIndex+1}页，
<c:if test="${page.pageIndex<=0}">首页&nbsp;上一页</c:if>
<c:if test="${page.pageIndex>0}">
	<a href="javascript:getPage(0);">首页</a>&nbsp;<a
		href="javascript:getPage(${page.pageIndex-1});">上一页</a>
</c:if>
<c:if test="${page.pageIndex<page.pageCount-1}">
	<a href="javascript:getPage(${page.pageIndex+1});">下一页</a>&nbsp;
				<a href="javascript:getPage(${page.pageCount-1});">尾页</a>
</c:if>
<c:if test="${page.pageIndex>=page.pageCount-1}">下一页&nbsp;尾页</c:if>
