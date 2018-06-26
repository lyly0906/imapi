<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/roomList?pageIndex=" + pageIndex + "&pageSize=25&roomName=" + $("#roomName").val();
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-bottom: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="25" />
					<div class="col-lg-3">
					 	<input id="roomName"
							name="roomName" type="text" class="form-control" placeholder="用户名字" value="${roomName}" />
					</div>
					<div style="margin-left:80px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a   href="/console/addRoom"><button  class="btn btn-info" type="button">新增群组</button></a>
					</div>
					<!-- 	
						<c:if test="${sessionScope.IS_ADMIN == 1}">
												<button type="button" class="btn btn-default btn-sm" onclick="deleteQueryResult();">清空用户</button> 
						</c:if>
					-->
				</form>
			</td>
	</table>
	<div class="backgrid-container">
		<table class="backgrid" style="background-color: #fff;">
			<thead>
				<tr>
					<th width="30"></th>
					<!-- <th width="10%">房间名字</th> -->
					<th width="10%">用户ID</th>
					<th width="10%">昵称</th>
					<!-- <th width="10%">角色</th> -->
					<th width="100">加入时间</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o.id}" /></td>
						<%-- <td class="string-cell">${o.userId}</td>
						<td class="string-cell">${o.desc}</td> --%>
						<td class="string-cell">${o.userId}</td>
						<td class="string-cell">${o.nickName}</td>
						<td>${utils:format(o.createTime*1000,'yyyy-MM-dd HH:mm')}</td>
						<td class="string-cell">
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteRoomUser?liveRoomId=${o.roomId}&userId=${o.userId}"'>踢出</a>
								<%-- &nbsp;&nbsp;<a href="/console/roomMsgDetail?room_jid_id=${o.roomId}">聊天记录</a>  --%>
								&nbsp;&nbsp;<a href='javascript:if(confirm("是否确认禁言？"))window.location.href="/console/shutup?userId=${o.userId}&type=1&roomId=${o.roomId}"'>禁言</a>
							</c:if>
						</td>
						<td>&nbsp;</td>
					</tr>
				</c:forEach>
				<tr>
					<td colspan="9"><jsp:include page="pageBar.jsp"></jsp:include></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/pages/js/jquery.min.js"></script>
<script type="text/javascript">
	$("#a1").click(function(){
		alert("haha");
		var userId=$("#userId").val();
		var type=1;
		$.ajax({
			type:"POST",
			url:"${pageContext.request.contextPath}/console/shutup",
			data:"userId="+userId&"type="+type,
			success:function(data){
				
			}
		})
	})
	
</script> --%>
<jsp:include page="bottom.jsp"></jsp:include>