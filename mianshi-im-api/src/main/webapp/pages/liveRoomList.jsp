<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/liveRoomList?pageIndex=" + pageIndex + "&pageSize=25&name=" + $("#liveroomName").val();
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-bottom: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="25" />
					<div class="col-lg-3">
					 	<input id="liveroomName"
							name="liveroomName" type="text" class="form-control" placeholder="房间名字" value="${name}" />
					</div>
					<div style="margin-left:80px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a  href="/console/addLiveRoom"><button  class="btn btn-info" type="button">新增直播</button></a>
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
					<th width="15"></th>
					<th width="15%">直播间名字</th>
					<th width="15%">房间公告</th>
					<th width="15%">创建者Id</th>
					<th width="15%">创建者</th>
					<th width="15%">推流地址</th>
					<th width="100">创建时间</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o.roomId}" /></td>
						<td class="string-cell">${o.name}</td>
						<td class="string-cell">${o.notice}</td>
						<td class="string-cell">${o.userId}</td>
						<td class="string-cell">${o.nickName}</td>
						<td class="string-cell">${o.url}</td>
						<td>${utils:format(o.createTime*1000,'yyyy-MM-dd HH:mm')}</td>
						<td class="string-cell">
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteLiveRoom?liveRoomId=${o.roomId}&pageIndex=${page.pageIndex}"'>删除</a>
								&nbsp;<a href="/console/roomMsgDetail?room_jid_id=${o.roomId}">聊天记录</a> 
								&nbsp;<a href="/console/">禁止送礼</a>
								&nbsp;<a href="/console/">禁播</a>
								&nbsp;<a href="/console/liveRoomUserManager?roomId=${o.roomId}">人员管理</a>
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
<jsp:include page="bottom.jsp"></jsp:include>