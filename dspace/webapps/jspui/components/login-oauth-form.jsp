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
	<div class="panel-body text-center">
            <div class="form-group ">
                <p><fmt:message key="jsp.login.oauth.email-incorrect"/></p> 
                <div>
                     <div class="g-signin2" style="display: inline-block;" data-onsuccess="onSignIn"></div>
                </div>
            </div>
        </div>
<script>
    function onSignIn(googleUser) {
        var profile = googleUser.getBasicProfile();
        var form=document.createElement("form");
        form.method="post";
        form.action="<%= request.getContextPath() %>/password-login";
        var input=document.createElement("input");
        input.type="hidden";
        input.name="email";
        input.value=googleUser.getAuthResponse().id_token;
        form.appendChild(input);
        form.submit();
    }
</script>