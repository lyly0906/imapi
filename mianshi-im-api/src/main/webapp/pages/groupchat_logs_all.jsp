<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/groupchat_logs_all?pageIndex=" + pageIndex + "&pageSize=25&room_jid_id=" + $("#room_jid_id").val();
	}
	function deleteQueryResult() {
		if (confirm("是否确认清空查询到的${page.total}条消息？")) {
			window.location.href = "/console/groupchat_logs_all/del?room_jid_id=" + $("#room_jid_id").val();
		}
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-top: 10px; margin-bottom: 10px; margin-left: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="25" /> <input id="room_jid_id"
						name="room_jid_id" type="text" class="form-control input-sm" placeholder="房间Id" value="${room_jid_id}" />
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
						<td width="180">房间Id</td>
						<td width="10%">发送者Id</td>
						<td width="10%">发送者</td>
						<td width="10%">时间</td>
						<td>内容</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach varStatus="i" var="o" items="${page.pageData}">
						<tr>
							<td>${o.room_jid_id}</td>
							<td>${o.sender}</td>
							<td>${o.fromUserName}</td>
							<td>${utils:format(o.ts,'yyyy-MM-dd HH:mm:ss')}</td>
							<td>${o.content}</td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="5"><jsp:include page="pageBar.jsp"></jsp:include></td>
					</tr>
				</tbody>
			</table>
		</div>
</div>


<jsp:include page="bottom.jsp"></jsp:include>