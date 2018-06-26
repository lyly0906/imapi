<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="col-sm-2"
	style="border: 1px #ddd solid; padding-left: 0px; padding-right: 0px; border-radius: 4px;">
	<ul class="nav nav-sidebar">
		<li><a id="a_home" href="/mp/home">首页</a></li>
		<li><a id="a_text" href="/mp/text">群发消息</a></li>
		<li><a id="a_push" href="/mp/push">群发单条图文</a></li>
		<li><a id="a_many" href="/mp/many">群发多条图文</a></li>
		<li><a id="a_menu" href="/mp/menuList">自定义菜单</a></li>
		<li><a id="a_msg" href="/mp/msg">消息管理</a></li>
		<li><a id="a_fans" href="/mp/fans">粉丝管理</a></li>
	</ul>
</div>
<script type="text/javascript">
	$(function() {
		var url = window.location.href;
		var id = url.substr(url.lastIndexOf('/') + 1);
		if (id.indexOf('.jsp') != -1)
			id = id.replace(/.jsp/g, "");
		console.log(id)
		$("#a_" + id).css({
			"color" : "#000",
			"font-weight" : "bold"
		});
	});
</script>