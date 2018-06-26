<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/giftList?pageIndex=" + pageIndex + "&pageSize=10&name=" + $("#name").val();
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-bottom: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="25" />
					<div class="col-lg-3">
					 	<input id="name"
							name="name" type="text" class="form-control" placeholder="礼物名称" value="${name}" />
					</div>
					<div style="margin-left:80px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a href="/console/aaddGift.jsp"><button  class="btn btn-info" type="button">新增礼物</button></a>
					</div>
				</form>
			</td>
	</table>
	<div class="backgrid-container">
		<table class="backgrid" style="background-color: #fff;">
			<thead>
				<tr>
					<th width="15"></th>
					<th width="15%">礼物名称</th>
					<th width="15%">礼物url</th>
					<th width="15%">礼物价格</th>
					<th width="15%">礼物类型</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o.giftId}" /></td>
						<td class="string-cell">${o.name}</td>
						<td class="string-cell">${o.photo}</td>
						<td class="string-cell">${o.price}</td>
						<td class="string-cell">${o.type}</td>
						<td class="string-cell">
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/delete/gift?giftId=${o.giftId}"'>删除礼物</a>
								&nbsp;<a href="/console/add/gift?gift">新增礼物</a> 
							</c:if>
						</td>
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