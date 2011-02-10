<html>
    <head>
        <title>Welcome to Grails</title>
		<meta name="layout" content="main" />
        <link rel="stylesheet" href="css/smoothness/jquery-ui-1.8.2.custom.css" type="text/css" media="all" /> 
    </head>
    <body>
        <h1 style="margin-left:20px;">Welcome to Grails</h1>
        <p style="margin-left:20px;width:80%">Congratulations, you have successfully started your first Grails application! At the moment
        this is the default page, feel free to modify it to either redirect to a controller or display whatever
        content you may choose. Below is a list of controllers that are currently deployed in this application,
        click on each to execute its default action:</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses}">
                    <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
              </g:each>
            </ul>
        </div>
      <g:compress method="jsmin">
        <script src="js/jquery-1.4.2.js" type="text/javascript"></script>

        <script src="js/jquery-ui-1.8.2.custom.min.js" type="text/javascript"></script>
        <script type="text/javascript">
        $(function() {
            $("#datepicker").datepicker();
        });
        </script>
      </g:compress>  
        <div class="demo">
           <p>Date: <input id="datepicker" type="text"></p>
        </div>
    </body>
</html>