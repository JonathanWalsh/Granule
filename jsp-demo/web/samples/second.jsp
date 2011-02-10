<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<html>
  <head><title>Simple jsp page</title>
  <script type="text/javascript">
     CLOSURE_BASE_PATH="<%=request.getContextPath()%>/js/closure/goog/";
  </script>    
  <g:compress>
      <script src="../js/common.js.js" type="text/javascript"></script>
      <script src="../js/closure/goog/base.js" type="text/javascript"></script>
      <script type="text/javascript">
          goog.require("goog.dom.annotate");
          var f=15;
          function piggy() {var s="kill piggy!!!";
          /* this is mega function */

          }
      </script>
  </g:compress>
  </head>
  <body>Place your content here</body>
</html>