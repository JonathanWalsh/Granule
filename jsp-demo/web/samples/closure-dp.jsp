<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<html>
  <head>
  <title>Closure DatePicker</title>
  
  <g:compress>
    <link rel="stylesheet" type="text/css" href="../js/closure/goog/css/datepicker.css"/>
    <link rel="stylesheet" type="text/css" href="../js/closure/goog/demos/css/demo.css"/>    
  </g:compress>
  
  </head>
  <body>
  <h2>Date Picker</h2>
  <div id="datepicker"></div>
  <div style="clear: both;">&nbsp;</div>
  <span id="label_dp"></span>
  <br><br>
  
  <g:compress>
    <script type="text/javascript" src="../js/common.js"></script>
    <script type="text/javascript" src="../js/closure/goog/base.js"></script>
    <script>
       goog.require('goog.dom');
       goog.require('goog.date');
       goog.require('goog.ui.DatePicker');
    </script>
  
    
  <script type="text/javascript">
    var dp = new goog.ui.DatePicker();
    dp.render(document.getElementById('datepicker'));

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

  

