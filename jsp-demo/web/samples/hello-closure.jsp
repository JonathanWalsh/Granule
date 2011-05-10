<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<html>

<head>
  <title>Hello World Google Closure Example</title>
  <g:compress>
     <link rel="stylesheet" type="text/css" href="../css/hello.css">
  </g:compress>
</head>

<body>
<div id="closure"></div>
<div id="hello"></div>
 <g:compress>
    <script src="../js/closure/goog/base.js"></script>
    <script src="../js/hello.js"></script>
    <script type="text/javascript">
        goog.require('example');
        example.sayHello('Hello, World!');
    </script>
 </g:compress>
</body>
</html>