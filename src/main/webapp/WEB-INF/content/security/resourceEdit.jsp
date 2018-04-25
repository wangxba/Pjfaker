<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
	<head>
		<title>资源管理</title>
		<%@ include file="/common/meta.jsp"%>
		<link rel="stylesheet" href="${ctx}/styles/css/style.css" type="text/css" media="all" />
		<link rel="stylesheet" type="text/css" href="${ctx}/styles/wbox/wbox/wbox.css" />
		<script type="text/javascript" src="${ctx}/styles/wbox/jquery-1.4.4.min.js"></script>
		<script type="text/javascript" src="${ctx}/styles/wbox/wbox.js"></script> 
		<script>
		var iframewbox;
	function openMenu() {
		iframewbox=$("#selectMenu").wBox({
			   	requestType: "iframe",
			   	iframeWH:{width:800,height:400},
			   	show: true,
			   	title:"选择菜单",
				target:"${ctx}/security/menu?lookup=1"
			   });
	}
	
	function callbackProcess(id, name) {
		if(iframewbox) {
			document.getElementById("parentMenuId").value=id;
			document.getElementById("parentMenuName").value=name;
			iframewbox.close();
		}
	}
</script>
	</head>

	<body>
		<form id="inputForm" action="${ctx }/security/resource/update" method="post">
			<input type="hidden" name="resource.id" id="id" value="${resource.id }"/>
		<table width="100%" border="0" align="center" cellpadding="0"
				class="table_all_border" cellspacing="0" style="margin-bottom: 0px;border-bottom: 0px">
			<tr>
				<td class="td_table_top" align="center">
					资源管理
				</td>
			</tr>
		</table>
		<table class="table_all" align="center" border="0" cellpadding="0"
			cellspacing="0" style="margin-top: 0px">
				<c:if test="${not empty nameMsg  || not empty sourceMsg}">
				<tr>
					<td class="td_table_1">
						<span>错误信息：</span>
					</td>
					<td class="td_table_2" colspan="3">
						<font color="red">${nameMsg }&nbsp;${sourceMsg }</font>
					</td>
				</tr>
				</c:if>
				<tr>
					<td class="td_table_1">
						<span>资源名称：</span>
					</td>
					<td class="td_table_2">
						<input type="text" class="input_240" id="name" name="resource.name"
							value="${resource.name }" />
					</td>
					<td class="td_table_1">
						<span>资源值：</span>
					</td>
					<td class="td_table_2">
						<input type="text" class="input_240" id="source" name="resource.source"
							value="${resource.source }" />
					</td>
				</tr>
 				<tr>
					<td class="td_table_1">
						<span>所属菜单：</span>
					</td>
					<td class="td_table_2" colspan="3">
						<input type="hidden" id="parentMenuId" name="resource.menu" value="${resource.menu }">
						<input type="text" id="parentMenuName" readonly="readonly" name="parentMenuName" class="input_240" value="${resource.menuName }">
						<input type='button' class='button_70px' value='选择菜单' id="selectMenu" onclick="openMenu()"/>
					</td>
				</tr>
			</table>
			<table align="center" border="0" cellpadding="0"
				cellspacing="0">
				<tr align="left">
					<td colspan="1">
						<input type="submit" class="button_70px" name="submit" value="提交">
						&nbsp;&nbsp;
						<input type="button" class="button_70px" name="reback" value="返回"
							onclick="history.back()">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
