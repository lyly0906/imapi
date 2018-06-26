<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/chat_logs_all?pageIndex=" + pageIndex + "&pageSize=25&sender=" + $("#sender").val() + "&receiver="
				+ $("#receiver").val();
	}
	function deleteQueryResult() {
		if (confirm("是否确认清空查询到的${page.total}条消息？")) {
			window.location.href = "/console/chat_logs_all/del?sender=" + $("#sender").val() + "&receiver=" + $("#receiver").val();
		}
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<table style="margin-top: 10px; margin-bottom: 10px; margin-left: 10px;">
			<tr>
				<td>
					<form class="form-inline">
						<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="25" /> <input id="sender"
							name="sender" type="text" class="form-control input-sm" placeholder="发送者" value="${sender}" /> <input id="receiver" name="receiver" type="text"
							class="form-control input-sm" placeholder="接收者" value="${receiver}" />
						<button type="submit" class="btn btn-default btn-sm">搜索消息</button>
						<c:if test="${sessionScope.IS_ADMIN == 1}">
							<button type="button" class="btn btn-default btn-sm" onclick="deleteQueryResult();">清空消息</button>
						</c:if>
					</form>
				</td>
		</table>
		<div class="backgrid-container">
			<table class="backgrid" style="background-color: #fff;">
				<thead>
					<tr>
						<th width="10%">发送者Id</th>
						<th width="10%">发送者</th>
						<th width="10%">接收者Id</th>
						<th width="10%">接收者</th>
						<th width="8%">时间</th>
						<th width="">内容</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach varStatus="i" var="o" items="${page.pageData}">
						<tr>
							<%-- <c:if test="${o.direction == 0}"> --%>
								<td>${o.sender}</td>
								<td>${o.sender_nickname}</td>
								<td>${o.receiver}</td>
								<td>${o.receiver_nickname}</td>
							<%-- </c:if> --%>
							<%-- <c:if test="${o.direction == 1}">
								<td>${o.receiver}</td>
								<td>${o.receiver_nickname}</td>
								<td>${o.sender}</td>
								<td>${o.sender_nickname}</td>
							</c:if> --%>
							<td>${utils:format(o.ts,'yyyy-MM-dd HH:mm:ss')}</td>
							<td>${o.content}</td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="6"><jsp:include page="pageBar.jsp"></jsp:include></td>
					</tr>
				</tbody>
			</table>
		</div>
</div>
<jsp:include page="bottom.jsp"></jsp:include>