<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>

<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
                       
</script>
<div style="margin-left: auto; margin-right: auto; width: 1000px; margin-top: 10px;">
	<form action="addkeyword" method="post" class="form-horizontal">
		      		<%--  <c:if test="${null!=o.id}">
		      		  	<input type="hidden" id="id" name="id" value="${o.id}"/>
		      		 </c:if>
		      		  <input type="hidden" id="userId" name="userId" value="${null==o.userId?10005:o.userId}"/> --%>
	      		  <div class="form-group">
					    <label  class="col-sm-2 control-label">敏感词汇</label>
					    <div class="col-sm-10">
					      <input name="word" class="form-control" value="word">
					    </div>
			  	  </div>
					   	<p>
							<button   class="btn btn-primary btn-lg" type="submit">保存</button>
						</p>
				        
	      		</form>
		  
</div>

<jsp:include page="bottom.jsp"></jsp:include>