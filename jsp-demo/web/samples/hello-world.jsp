<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<html>

<head>
 <title>Hello world google closure example</title>
 <g:compress>
   <link rel="stylesheet" type="text/css" href="../css/hello.css">
 </g:compress>  
</head>

<body>
 <div id="hello"></div>
 <g:compress>
    <script type="text/javascript">
        document.getElementById('hello').innerHTML= 'Hello, World!';
    </script>
 </g:compress>
</body>

</html>