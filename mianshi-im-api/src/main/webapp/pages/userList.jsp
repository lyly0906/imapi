<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/userList?pageIndex=" + pageIndex + "&pageSize=25&sender=" + $("#nickname").val()+
				"&onlinestate="+$("#onlinestate").val();
	}
	function deleteQueryResult() {
		if (confirm("是否确认清空查询到的${page.total}个用户？")) {
			window.location.href = "/console/deleteUser?pageIndex=${page.pageIndex}&nickname=" + $("#nickname").val();
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
						<input id="nickname"
								name="nickname" type="text" class="form-control" placeholder="用户昵称" value="${nickname}" />
					</div>
					<div style="margin-left:30px" class="col-lg-3">
						<select class="form-control" id="onlinestate" name="onlinestate">
							<option value="0">在线状态</option>
							<option value="1" <c:if test="${param.onlinestate==1}">selected</c:if> >
								在线
							 </option>
						</select>
					</div>
					<div style="margin-left:30px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a id=“userList"  href="/console/updateUser"><button  class="btn btn-info" type="button">新增用户</button></a>
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
					<th width="30"></th>
					<th width="100">用户Id</th>
					<th width="200">昵称</th>
					<th width="100">注册时间</th>
					<th width="100">在线状态</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o.userId}" /></td>
						<td>${o.userId}</td>
						<td>${o.nickname}</td>
						<td>${utils:format(o.createTime*1000,'yyyy-MM-dd HH:mm:ss')}</td>
						<td>${1==o.onlinestate?"在线":"离线"}</td>
						<td>
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteUser
									?userId=${o.userId}&pageIndex=${page.pageIndex}"'>删除</a>
								<a href="/console/updateUser
									?userId=${o.userId}">修改</a>
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