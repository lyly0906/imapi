<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<link href="/pages/plugins/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet">
<style>
.yellow{
background-color:yellow;
}
</style>
<script type="text/javascript" src="/pages/js/echarts.min.js"></script>
<script type="text/javascript" src="/pages/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script> 
<script type="text/javascript"> 
	Date.prototype.Format = function (fmt) { //author: meizz 
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	}
</script>
<body>
			 <div class="search" style="width:100%;">
				  	<form action="" >
						<label class=" startTimeLab"  style="margin-top:0px;">
							<span>开始时间：</span>
							<input id="startTime" class="datepicker" name="startTime" value="${param.startTime}"  type="text">
						</label>
					  	<label class=" endTimeLab" style="margin-top:0px;">
						  	<span>结束时间：</span>
						  	<input id="endTime" class="datepicker" name="endTime" value="${param.endTime}" type="text" >
					  	</label>
					  	<button type="button" onclick="serachSign()" id="searchButton" class="btn btn-info" style="margin-top:0px;">搜索</button>
					</form>
			  </div>
		
	   <div class="btn-group"  style="margin-bottom:25px;margin-left:14px;">
	    			<button type="button" onclick="serachSign(-3,3)" class="btn btn-default green sign">最近一个月(天)</button>
    				<button type="button" onclick="serachSign(-2,3)" class="btn btn-default green sign">最近7天(天)</button>
    				<button type="button" onclick="serachSign(-2,2)" class="btn btn-default green sign">最近7天(小时)</button>
    				<button type="button" onclick="serachSign(-1,2)" class="btn btn-default green sign yellow">最近48小时</button>
    				<button type="button" onclick="serachSign(-1,1)" class="btn btn-default green sign">最近48小时(分钟)</button>
				 	<button type="button" onclick="serachSign(0,2)" class="btn btn-default green sign">今日(小时)</button>
				  <button type="button" onclick="serachSign(0,1)" class="btn btn-default green sign">今日(分钟)</button>
				  <button type="button" onclick="serachSign(3,3)" class="btn btn-default green sign">所有</button>
				 <span id="sign" style="display:none;">${param.sign}</span> 
		</div>
    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="main"  style="width:1500px;height:600px; margin-left:50px;margin-top:60px;"></div>
<script type="text/javascript">
//基于准备好的dom，初始化echarts实例 ss
var myChart = echarts.init(document.getElementById('main'));
var data={
		time:new Array(),
		value:new Array()
		
};

$(function(){
	initDatepicker();
	initChart();
	  serachSign(-1,2);
	   
	    $(".sign").click(function(){
		   	 $(".sign").removeClass("yellow");
		   	 $(this).addClass("yellow");
	    });
});

//初始化时间插件 Start
 function initDatepicker(){
	 $('.datepicker').datetimepicker({
	        minView: "month", //选择日期后，不会再跳转去选择时分秒 
	        format: "yyyy-mm-dd", //选择日期后，文本框显示的日期格式 
	        language: 'zh-CN', //汉化  
	        showMeridian : true,//是否加上网格
	        autoclose:true, //选择日期后自动关闭 
	        weekStart: 1,
	        todayBtn:  1,
	        // todayHighlight: 0,
	 });
	  
}//初始化时间插件 End

//初始化  EChart start
function initChart(){
	myChart.setOption({
		 title: {
		        text: '在线数量走势图'
		    },
      tooltip: {
    	enterable:true, 
    	trigger:"axis",
    	 backgroundColor : 'rgba(255,0,255,0.7)',
         borderColor : '#f50',
    	formatter: function (params,ticket,callback) {
             
               var res = '时间:' + params[0].name;
              res += '<br/>数量:  ' + params[0].value;
               setTimeout(function (){
                   // 仅为了模拟异步回调
                   callback(ticket, res);
               }, 500)
               return 'loading';
           }
	  },
       legend: {
           data:['数量']
       },
       xAxis: {
       	name:"时间",
           data:[],
           splitLine: {
               show: true
           }
         	/* axisLabel: {
        	        interval:0,//横轴信息全部显示
        	        rotate: 0,//60度角倾斜显示
        	     formatter:function(val){
        	        return val.split("").join("\n"); //横轴信息文字竖直显示
        	   }
         	} */
       },
       yAxis: {
       	name:"数量",
       	 type: 'value',
       	 splitLine: {
                show: true
            }
       },
       toolbox: {
           left: 'center',
           feature: {
               dataZoom: {
                   yAxisIndex: 'none'
               },
               restore: {},
               saveAsImage: {}
           }
       },
       series: [{
           type: 'line',
           data: [],
           markLine: {
               silent: true,
               data: [{
                   yAxis: 300
               }, {
                   yAxis: 500
               }, {
                   yAxis: 700
               }, {
                   yAxis: 900
               }]
           }
       }]
	});
}//初始化  EChart End
      
 function serachSign(sign,type){
	//http://192.168.0.168:8092/
	 var codeUrl="";
		if(sign==null){
			var startDate = $("input[name='startTime']").val();
			var endDate = $("input[name='endTime']").val();
			if(startDate == '' || endDate == ''){
				bootbox.alert("请选择时间段");
				return false;
			}else{
				var start = new Date(startDate);
				var end = new Date(endDate);
				if((end-start)<0){
					alert("请选择正确的时间段");
					return false;
				}
			}
			codeUrl="${ctx}/user/getUserStatusCount?startDate="+startDate+"&endDate="+endDate+"&type="+3;;
		}else 
		 codeUrl="${ctx}/user/getUserStatusCount?sign="+sign+"&type="+type;
		 
		 $.ajax({
			    type:"GET",
			    url:codeUrl,
			    dataType:"json",
			    success:function(map){
			    	 data.time=[];
					 data.value=[];
			    	 var list=map.data;
					 var time=null;
					   for ( var index in list) {
						  if(3==type)
							time= new Date(list[index].time*1000).Format("yy-MM-dd");
						  else if(2==type)
							  time= new Date(list[index].time*1000).Format("MM-dd hh:00");
						  else time= new Date(list[index].time*1000).Format("MM-dd hh:mm");
						   data.time.push(time);
						   data.value.push(list[index].count)
						}
						//data.time=result.times;
						
						refreshData();
			     
			 }
			});
		 
		 
		 
	/*  $.get(codeUrl, function(result){
		 var times=result.data.times;
		 var time=null;
		   for ( var index in times) {
				time= new Date(times[index]).Format("MM-dd hh:mm"); ;
			   data.time.push(time);
			  
			}
			//data.time=result.times;
			data.value=result.data.values;
			refreshData();
		}); */
	 
}
		
function refreshData(){
	 // 指定图表的配置项和数据
    var option = {
		dataZoom: [ 
					 {
			            id: 'dataZoomX',
			            show:true,
			            type: 'slider',
			          	xAxisIndex:0,
			            filterMode: 'empty'
			        }
				],
		xAxis: {
			name:"时间",
            data:data.time,
         },
       series: [{
    		name:'数量',
          data: data.value
        }],
		
    };
	 // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

		 
       
		 
</script>
</body>
</html>
</body>
<jsp:include page="bottom.jsp"></jsp:include>