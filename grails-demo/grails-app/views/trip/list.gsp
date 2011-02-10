

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Trip List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Trip</g:link></span>
        </div>
        <div class="body">        
            <h1>Trip List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <th>Airline</th>
                   	    
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="city" title="City" />
                        
                   	        <g:sortableColumn property="startDate" title="Start Date" />
                        
                   	        <g:sortableColumn property="endDate" title="End Date" />
                        
                        </tr>
                    </thead>
                    <tbody>
<g:each in="${tripList}" status="i" var="trip">
  <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">                       
    <td><g:link action="show" id="${trip.id}">${trip.id?.encodeAsHTML()}</g:link></td>                      
    <td>${trip.airline?.encodeAsHTML()}</td>                        
    <td>${trip.name?.encodeAsHTML()}</td>                        
    <td>${trip.city?.encodeAsHTML()}</td>                        
    <td>${trip.startDate?.encodeAsHTML()}</td>                        
    <td>${trip.endDate?.encodeAsHTML()}</td>                        
  </tr>
</g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${Trip.count()}" />
            </div>
        </div>
        

<g:render template="/footer" />

    </body>
</html>
