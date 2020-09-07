<!DOCTYPE html>
<html lang="en" prefix="og: https://ogp.me/ns#">
<head>
	<%include "fragment-header.gsp"%>
</head>
<body onload="prettyPrint()">

	<%include 'fragment-menu.gsp'%>
	<div class="main-content">

	<h1>${content.title}</h1>

	<p>${content.body}</p>

	</div>
<%include "fragment-footer.gsp"%>
</html>
