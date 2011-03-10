<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title>
    <style type="text/css">
        @import "../js/dojo-150/dijit/themes/tundra/tundra.css";
        @import "../js/dojo-150/dojo/resources/dojo.css";
    </style>


</head>
<body class="tundra">
<script type="text/javascript">
    var djConfig = {
        parseOnLoad: true,
        baseUrl: "<%=request.getContextPath()%>/js/dojo-150/dojo/"
    };
</script>
<g:compress>
<script type="text/javascript" src="../js/dojo-150/dojo/dojo.js"></script>
<script type="text/javascript">
    dojo.require("dijit.form.DateTextBox");
</script>
</g:compress>
<input type="text" name="date" dojoType="dijit.form.DateTextBox" required="true"/>
</body>
</html>