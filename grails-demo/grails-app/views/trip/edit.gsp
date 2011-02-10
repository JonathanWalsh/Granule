

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Trip</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Trip List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Trip</g:link></span>
        </div>
        <div class="body">
            <h1>Edit Trip</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${trip}">
            <div class="errors">
                <g:renderErrors bean="${trip}" as="list" />
            </div>
            </g:hasErrors>
            <g:form controller="trip" method="post" >
                <input type="hidden" name="id" value="${trip?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="airline">Airline:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'airline','errors')}">
                                    <g:select optionKey="id" from="${Airline.list()}" name="airline.id" value="${trip?.airline?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:trip,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="city">City:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'city','errors')}">
                                    <input type="text" id="city" name="city" value="${fieldValue(bean:trip,field:'city')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="startDate">Start Date:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'startDate','errors')}">
                                    <g:datePicker name="startDate" value="${trip?.startDate}" ></g:datePicker>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="endDate">End Date:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'endDate','errors')}">
                                    <g:datePicker name="endDate" value="${trip?.endDate}" ></g:datePicker>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="purpose">Purpose:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'purpose','errors')}">
                                    <g:select id="purpose" name="purpose" from="${trip.constraints.purpose.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:trip,field:'purpose')}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="notes">Notes:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:trip,field:'notes','errors')}">
                                    <textarea rows="5" cols="40" name="notes">${trip?.notes?.encodeAsHTML()}</textarea>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
