<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title>
<g:compress>
  <link rel="stylesheet" href="../css/smoothness/jquery-ui-1.8.2.custom.css" type="text/css" media="all" />
</g:compress>
</head>
<body>Place your content here<br>
    <g:compress method="jsfastmin">
    <script src="../js/jquery-1.4.2.js" type="text/javascript"></script>
  
    <script src="../js/jquery-ui-1.8.2.custom.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(function() {
            $("#datepicker").datepicker();
        });
    </script>
    </g:compress>
<div class="demo">
    <p>Date: <input id="datepicker" type="text"></p>
</div>
<!-- End demo -->
<div style="display: none;" class="demo-description">
    <p>The datepicker is tied to a standard form input field. Focus on the input (click, or use the tab key) to open an
        interactive calendar in a small overlay. Choose a date, click elsewhere on the page (blur the input), or hit the
        Esc key to close. If a date is chosen, feedback is shown as the input's value.</p>
</div>
</body>
</html>