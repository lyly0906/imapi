<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getPage(pageIndex) {
		window.location.href = "/console/keywordfilter?pageIndex=" + pageIndex + "&pageSize=10&name=" + $("#keyword").val();
	}
</script>
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<table style="margin-bottom: 10px;">
		<tr>
			<td>
				<form class="form-inline">
					<input id="pageIndex" name="pageIndex" type="hidden" value="0" /> <input id="pageSize" name="pageSize" type="hidden" value="10" />
					<div class="col-lg-3">
					 	<input id="keyword"
							name="keyword" type="text" class="form-control" placeholder="敏感词" value="${keyword}" />
					</div>
					<div style="margin-left:80px" class="col-lg-1">
						<button type="submit"  class="btn btn-danger">搜索</button>
					</div>
					<div style="margin-left:30px" class="col-lg-3">
					 <a  href="/console/keywordEdit"><button  class="btn btn-info" type="button">新增敏感词</button></a>
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
					<th width="15%">敏感词</th>
					<th width="15%">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o._id}" /></td>
						<td class="string-cell">${o.word}</td>
						<td class="string-cell">
							<c:if test="${sessionScope.IS_ADMIN == 1}">
								<a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deletekeyword?id=${o._id}"'>删除</a>
								&nbsp;<a href="/console/addkeyword?id=${o._id}">修改</a> 
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