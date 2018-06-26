<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls"
	prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<form action="/console/pushToAll" method="post" enctype="application/x-www-form-urlencoded">
	<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<b>发送系统消息</b><br><br><br>
				<input type="radio" value="10000" name="fromUserId" checked="checked" /> 10000&nbsp;&nbsp;<!-- <input type="radio" value="10005" name="fromUserId" /> 10005 -->
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<textarea class="form-control" rows="3" id="body" name="body" required="required">{"content":"推送内容","fromUserId":"10000","fromUserName":"系统消息","type":1,"timeSend":""}</textarea>
			</div>
		</div>
		<c:if test="${sessionScope.IS_ADMIN == 1}">
			<div class="row" style="margin-top: 10px;">
				<div class="col-md-12" style="text-align: center;">
					<button type="submit" class="btn btn-success">&nbsp;批量发送&nbsp;</button>
				</div>
			</div>
		</c:if>
	</div>
</form>
<form action="/console/sendMessage" method="post" enctype="application/x-www-form-urlencoded">
	<div class="container" style="margin-top: 100px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<div class="row" style="margin-top: 10px;">
				
			<div class="col-md-12" style="text-align: center;">
				<b>根据数量选取指定好友人数  每个人向接受者发送者发送文本框中的消息</b><br><br><br>
				接受者ID:<input type="text" value="" name="to" />
				发送数量：<input type="text" value="" name="count" /><br><br>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<textarea class="form-control" rows="3"  name="body" required="required"></textarea>
			</div>
		</div>
		<c:if test="${sessionScope.IS_ADMIN == 1}">
			<div class="row" style="margin-top: 10px;">
				<div class="col-md-12" style="text-align: center;">
					<button type="submit" class="btn btn-success">&nbsp;批量发送&nbsp;</button>
				</div>
			</div>
		</c:if>
	</div>
</form>
<jsp:include page="bottom.jsp"></jsp:include>