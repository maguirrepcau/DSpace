<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Component which displays a login form and associated information
  --%>
  
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>
    <div class="panel-body">
        <div class="form-group text-center">
            <div>
                <p><fmt:message key="jsp.login.oauth.email-incorrect"/></p> 
                <a href="#" onclick="signOut();">Sign out</a>
            </div>
        </div>
    </div>    