<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>群发消息</title>
<link
	href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="/pages/css/backgrid.css" rel="stylesheet" />
<link href="/pages/css/css.css" rel="stylesheet" />
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<!-- <script src="http://v3.bootcss.com/assets/js/ie-emulation-modes-warning.js"></script> -->
<!-- <script src="/js/bootstrap/assets/js/vendor/holder.min.js"></script> -->
<!-- <script src="/js/bootstrap/assets/js/ie10-viewport-bug-workaround.js"></script> -->
<!--[if lt IE 9]>
<script src="http://v3.bootcss.com/assets/js/ie8-responsive-file-warning.js"></script>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<style type="text/css">
</style>
<script type="text/javascript">
	function pushToAll() {
		var body=$("#body").val();
		/* alert(body); */
		$.ajax({
			url : '/mp/pushToAll',
			method : 'POST',
			data : {
				body : body
			},
			success : function(result) {
				if (result.resultCode == 1) {
					alert("群发成功");
					$("#body").val("");
				} else {
					alert("群发失败");
				}
			},
			error : function() {
				alert("群发失败");
			}
		})
	}
</script>
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<div class="container"
		style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<div class="row">
				<div class="col-md-12">
					<textarea class="form-control" rows="3" id="body" name="body" required="required"></textarea>
				</div>
			</div>
			<div class="row" style="margin-top: 10px;">
				<div class="col-md-12" style="text-align: center;">
					<button class="btn btn-success" onclick="pushToAll()">&nbsp;群发&nbsp;</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
