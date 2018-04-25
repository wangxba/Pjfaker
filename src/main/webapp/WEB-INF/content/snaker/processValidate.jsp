<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
	<head>
		<title>流程状态</title>
		<%@ include file="/common/meta.jsp"%>
		<link rel="stylesheet" href="${ctx}/styles/css/style.css" type="text/css" media="all" />
		<link rel="stylesheet" href="${ctx}/styles/css/snaker.css" type="text/css" media="all" />
		<script src="${ctx}/styles/js/raphael-min.js" type="text/javascript"></script>
		<script src="${ctx}/styles/js/jquery-ui-1.8.4.custom/js/jquery.min.js" type="text/javascript"></script>
		<script src="${ctx}/styles/js/jquery-ui-1.8.4.custom/js/jquery-ui.min.js" type="text/javascript"></script>
		<script src="${ctx}/styles/js/snaker/snaker.designer.js" type="text/javascript"></script>
		<script src="${ctx}/styles/js/snaker/snaker.model.js" type="text/javascript"></script>
		<script src="${ctx}/styles/js/snaker/snaker.editors.js" type="text/javascript"></script>

<script type="text/javascript">
	function display(process, active) {
		/** view*/
		$('#snakerflow').snakerflow($.extend(true,{
			basePath : "${ctx}/styles/js/snaker/",
            ctxPath : "${ctx}",
            orderId : "${order.id}",
			restore : eval("(" + process + ")")
			,
			editable : false
			},eval("(" + active + ")")
		));
	}
	
	function validateData() {
		var content = $('#content').val();
		display(content,"{'activeRects':{'rects':[{'paths':[],'name':'approveBoss'}]}}");
	}
</script>
</head>
	<body>
		<table class="table_all" align="center" border="0" cellpadding="0"
			cellspacing="0" style="margin-top: 0px" width="98%">
			<tr>
				<td>
					<textarea rows="20" cols="100" id="content"></textarea>
				</td>
			</tr>
		</table>
		<table align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left">
					<input type='button' class='button_70px' value='验证' onclick="validateData()"/>
				</td>
			</tr>
		</table>
		<table class="properties_all" align="center" border="1" cellpadding="0" cellspacing="0" style="margin-top: 0px">
			<div id="snakerflow" style="border: 1px solid #d2dde2; margin-top:10px; margin-left:10px; width:98%;">
			</div>
		</table>
	</body>
</html>
