<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<html>
<head><title>Simple jsp page</title>
    <link rel="stylesheet" href="../js/closure/goog/css/datepicker.css" type="text/css" media="all"/>
    <link rel="stylesheet" href="../js/closure/goog/demos/css/demo.css" type="text/css" media="all"/>
    <link rel="stylesheet" href="../css/smoothness/jquery-ui-1.8.2.custom.css" type="text/css" media="all"/>
</head>
<body>
<script type="text/javascript">
    CLOSURE_BASE_PATH = "<%=request.getContextPath()%>/js/closure/goog/";
</script>
<g:compress method="closure-compiler" options="--compilation_level SIMPLE_OPTIMIZATIONS --output_wrapper (function(){%output%})();">
    <script src="../js/closure/goog/base.js"></script>
    <script type="text/javascript">
        goog.require('goog.dom');

        function createHeader()
        {
            var newHeader = goog.dom.createDom('h1', {'style': 'background-color:#EEE'},
                    'Date picker!');
            goog.dom.appendChild(document.body, newHeader);
        }
        createHeader();
    </script>
</g:compress>


<div id="widget_dp"></div>
<div style="clear: both;">&nbsp;</div>
<span id="label_dp"></span>
<br><br>

<g:compress method="closure-compiler" options="--compilation_level SIMPLE_OPTIMIZATIONS --output_wrapper (function(){%output%})();">
    <script src="../js/closure/goog/base.js"></script>

    <script>
        goog.require('goog.dom');
        goog.require('goog.date');
        goog.require('goog.i18n.DateTimeSymbols');
        goog.require('goog.ui.DatePicker');
    </script>
    <script type="text/javascript">
        var dp = new goog.ui.DatePicker();
        dp.render(document.getElementById('widget_dp'));

        goog.events.listen(dp, goog.ui.DatePicker.Events.CHANGE,
                function(event)
                {
                    goog.dom.setTextContent(document.getElementById('label_dp'), event.date ?
                            event.date.toIsoString(true) : 'none');
                });

        goog.dom.setTextContent(document.getElementById('label_dp'),
                dp.getDate().toIsoString(true));
    </script>
</g:compress>
<%--
  com.granule.CompressTag.addContent(request,"jquery"," <script src='../js/jquery-ui-1.8.2.custom.min.js' type='text/javascript'></script>");
--%>
<g:compress id="jquery" method="jsfastmin">
    <script src="../js/jquery-1.4.2.js" type="text/javascript"></script>
    <script src='../js/jquery-ui-1.8.2.custom.min.js' type='text/javascript'></script>
</g:compress>

<script type="text/javascript">
        $(function()
        {
            $("#datepicker").datepicker();
        });
    </script>
<div class="demo">
    <p>Date: <input id="datepicker" type="text"></p>
</div>
</body>
</html>