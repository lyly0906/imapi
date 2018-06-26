<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/redPacketList?pageIndex=" + pageIndex + "&pageSize=25&userId=" + $("#userId").val();
	}
	function deleteQueryResult() {
		if (confirm("是否确认清空查询到的${page.total}个用户？")) {
			window.location.href = "/console/deleteUser?pageIndex=${page.pageIndex}&userId=" + $("#userId").val();
		}
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-bottom: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> 
					<input id="pageSize" name="pageSize" type="hidden" value="25" /> 
					<div class="col-lg-3">
						<input id="userId"
								name="userId" type="text" class="form-control" placeholder="用户ID" value="${userId}" />
					</div>
					<div style="margin-left:80px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a id=“redPacket"  href="/console/updateUser"><button  class="btn btn-info" type="button">新增用户</button></a>
					</div>
			
					<c:if test="${sessionScope.IS_ADMIN == 1}">
						<!-- 						<button type="button" class="btn btn-default btn-sm" onclick="deleteQueryResult();">清空用户</button> -->
					</c:if>
				</form>
			</td>
	</table>
	<div class="backgrid-container">
		<table class="backgrid" style="background-color: #fff;">
			<thead>
				<tr>
					<th width="30">用户Id</th>
					<th width="100">昵称</th>
					<th width="200">金额</th>
					<th width="100">发送时间</th>
					<th width="100">状态</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td>${o.userId}</td>
						<td>${o.userName}</td>
						<td>${o.money}</td>
						<td>${utils:format(o.sendTime*1000,'yyyy-MM-dd HH:mm:ss')}</td>
						<td>${2==o.status?"已领完":-1==o.status?"已退款":"未领完"}</td>
						<td>
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<%-- <a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteUser
									?userId=${o.userId}&pageIndex=${page.pageIndex}"'>删除</a> --%>
								<%-- <a href="/console/updateUser
									?userId=${o.userId}">修改</a> --%>
							</c:if>
						</td>
						<td></td>
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