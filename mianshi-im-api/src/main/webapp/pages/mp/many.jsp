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
<title>群发多条图文消息</title>
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
</head>
<body>
	<jsp:include page="top.jsp"></jsp:include>
	<div class="container"
		style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<%@ include file="left.jsp"%>
		<div class="col-sm-10">
			<div class="row">
				<div class="col-md-12">
				<form action="/mp/manyToAll">
					<table id="tb" >
						<tr>
							<td>标题</td>
							<td>图片url</td>
							<td>网页url</td>
						</tr>
						<tr>
							<td><textarea style="width: 200px" rows="1" id="body" name="title" required="required"></textarea></td>
							<td><textarea style="width: 500px" rows="1" id="body2" name="img" required="required"></textarea></td>
							<td><textarea style="width: 500px" rows="1" id="body3" name="url" required="required"></textarea></td>
						</tr>
					</table>
					</div>
				</div>
				<div class="row" style="margin-top: 10px;">
					<div class="col-md-12" style="text-align: center;">
						<button class="btn btn-success" onclick="add()">&nbsp;新增&nbsp;</button>
						<button class="btn btn-success" type="submit">&nbsp;群发&nbsp;</button>
					</div>
				</div>
			</form>
		</div>
	</div>
<script type="text/javascript">
	var i=1;
	function pushToAll() {
		var title=new Array();
		title.push($("#body").val());
		var url=new Array();
		url.push($("#body3").val());
		var img=new Array();
		img.push($("#body2").val());
		
		for(var j=2;j<=i;j++){
			title.push($("#f"+j).val());
			url.push($("#d"+j).val());
			img.push($("#c"+j).val());
		}
		alert(title);
		$.ajax({
			url : '/mp/manyToAll',
			method : 'POST',
			data : {
				title:title,
				url:url,
				img:img
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
	};
	
	function add(){
		i++;
		var table="<tr><td>标题</td><td>图片url</td><td>网页url</td></tr><tr></tr>"+
		"<tr><td><textarea style='width: 200px' rows='1' id='f"+i+"' name='title' required='required'></textarea></td>"+
		"<td><textarea style='width: 500px' rows='1' id='c"+i+"' name='img' required='required'></textarea></td>"+
		"<td><textarea style='width: 500px' rows='1' id='d"+i+"' name='url' required='required'></textarea></td></tr>";
		$("#tb").append(table);
	/* 	alert(i); */
	}
	
</script>
</body>
</html>
