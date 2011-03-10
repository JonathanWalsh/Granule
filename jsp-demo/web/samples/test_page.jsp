<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Simple jsp page</title>
  <link rel="stylesheet" href="../js/closure/goog/css/datepicker.css" type="text/css" media="all" />
  <link rel="stylesheet" href="../js/closure/goog/demos/css/demo.css" type="text/css" media="all" />
  </head>
  <body>
  <h2>Date Picker</h2>
  <div id="widget_dp"></div>
  <div style="clear: both;">&nbsp;</div>
  <span id="label_dp"></span>
  <br><br>
  
  <%@include file="includes/test1.inc"%>

  <g:compress method="closure-compiler">
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
        function(event) {
      goog.dom.setTextContent(document.getElementById('label_dp'), event.date ?
          event.date.toIsoString(true) : 'none');
    });
    goog.dom.setTextContent(document.getElementById('label_dp'),
                            dp.getDate().toIsoString(true));
   </script>
  </g:compress>
  </body>
</html>