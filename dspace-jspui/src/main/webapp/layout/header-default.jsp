<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - HTML header for main home page
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="org.dspace.app.webui.util.JSPManager" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.app.util.Util" %>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>

<%
    String title = (String) request.getAttribute("dspace.layout.title");
    String navbar = (String) request.getAttribute("dspace.layout.navbar");
    boolean locbar = ((Boolean) request.getAttribute("dspace.layout.locbar")).booleanValue();

    String siteName = ConfigurationManager.getProperty("dspace.name");
    String feedRef = (String)request.getAttribute("dspace.layout.feedref");
    boolean osLink = ConfigurationManager.getBooleanProperty("websvc.opensearch.autolink");
    String osCtx = ConfigurationManager.getProperty("websvc.opensearch.svccontext");
    String osName = ConfigurationManager.getProperty("websvc.opensearch.shortname");
    List parts = (List)request.getAttribute("dspace.layout.linkparts");
    String extraHeadData = (String)request.getAttribute("dspace.layout.head");
    String extraHeadDataLast = (String)request.getAttribute("dspace.layout.head.last");
    String dsVersion = Util.getSourceVersion();
    String generator = dsVersion == null ? "DSpace" : "DSpace "+dsVersion;
    String analyticsKey = ConfigurationManager.getProperty("jspui.google.analytics.key");
%>

<!DOCTYPE html>
<html>
    <head>
        <title><%= siteName %>: <%= title %></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="Generator" content="<%= generator %>" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="google-signin-client_id" content="921461597171-9cqd1p15ulpepe83d7imo8gjh24kpnge.apps.googleusercontent.com">
        <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon"/>
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/jquery-ui-1.10.3.custom/redmond/jquery-ui-1.10.3.custom.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/bootstrap.min.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/bootstrap-theme.min.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/dspace-theme.css" type="text/css" />
<%
    if (!"NONE".equals(feedRef))
    {
        for (int i = 0; i < parts.size(); i+= 3)
        {
%>
        <link rel="alternate" type="application/<%= (String)parts.get(i) %>" title="<%= (String)parts.get(i+1) %>" href="<%= request.getContextPath() %>/feed/<%= (String)parts.get(i+2) %>/<%= feedRef %>"/>
<%
        }
    }
    
    if (osLink)
    {
%>
        <link rel="search" type="application/opensearchdescription+xml" href="<%= request.getContextPath() %>/<%= osCtx %>description.xml" title="<%= osName %>"/>
<%
    }

    if (extraHeadData != null)
        { %>
<%= extraHeadData %>
<%
        }
%>
        
	<script type='text/javascript' src="<%= request.getContextPath() %>/static/js/jquery/jquery-1.10.2.min.js"></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/jquery/jquery-ui-1.10.3.custom.min.js'></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/bootstrap/bootstrap.min.js'></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/holder.js'></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/utils.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/static/js/choice-support.js"> </script>
    
    <script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>
    <script>
            function signOut() {
                    var win = window.open('<%= request.getContextPath() %>/logout', '_blank');
                    document.location="https://mail.google.com/mail/u/0/?logout&hl=en";
                }
    </script>

    <%--Gooogle Analytics recording.--%>
    <%
    if (analyticsKey != null && analyticsKey.length() > 0)
    {
    %>
        <script type="text/javascript">
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '<%= analyticsKey %>']);
            _gaq.push(['_trackPageview']);

            (function() {
                var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
            })();
        </script>
    <%
    }
    if (extraHeadDataLast != null)
    { %>
		<%= extraHeadDataLast %>
		<%
		    }
    %>
    

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="<%= request.getContextPath() %>/static/js/html5shiv.js"></script>
  <script src="<%= request.getContextPath() %>/static/js/respond.min.js"></script>
<![endif]-->
<script type='text/JavaScript'>
function blmostrocult(blconted){
var c=blconted.nextSibling;
if(c.style.display=='none'){
c.style.display='block';
} else {
c.style.display='none';
}
return false;
}
</script>
    </head>

    <%-- HACK: leftmargin, topmargin: for non-CSS compliant Microsoft IE browser --%>
    <%-- HACK: marginwidth, marginheight: for non-CSS compliant Netscape browser --%>
    <body class="undernavigation">
<a class="sr-only" href="#content">Skip navigation</a>
<header class="navbar navbar-inverse navbar-fixed-top">    
    <%
    if (!navbar.equals("off"))
    {
%>
            <div class="container">
                <dspace:include page="<%= navbar %>" />
            </div>
<%
    }
    else
    {
    	%>
        <div class="container">
            <dspace:include page="/layout/navbar-minimal.jsp" />
        </div>
<%    	
    }
%>
</header>

<div id="myHeader" class="myclearfix">
	<div id="myHeaderBox">
	<br>
    	<div id="headerLogo"><img src="<%= request.getContextPath() %>/image/encabezado.png" alt=""></div>
    	<div id="tagline">Repositorio Institucional Continental</div>
		<br>
    </div>
    <div id="bigBtns">
    	<div id="myHeaderBox" >
		<br>
      <ul>
          <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=%2F&query=Tesis&rpp=10&sort_by=score&order=desc&filter_field_1=subject&filter_type_1=equals&filter_value_1=Tesis"><img src="<%= request.getContextPath() %>/image/tesis.png" alt=""><span>Tesis</span></a></li>
		  <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=%2F&query=Sílabos&rpp=10&sort_by=score&order=desc&filter_field_1=subject&filter_type_1=equals&filter_value_1=Sílabos"><img src="<%= request.getContextPath() %>/image/proyectos.png" alt=""><span>Sílabos</span></a></li>
		  <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=%2F&query=Libros&rpp=10&sort_by=score&order=desc&filter_field_1=subject&filter_type_1=equals&filter_value_1=Libros"><img src="<%= request.getContextPath() %>/image/libros.png" alt=""><span>Libros</span></a></li>
		  <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=123456789%2F132&query=&rpp=10&sort_by=score&order=desc"><img src="<%= request.getContextPath() %>/image/revista.png" alt=""><span>Revistas</span></a></li>		   
          <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=%2F&query=Polimedia&rpp=10&sort_by=score&order=desc&filter_field_1=subject&filter_type_1=equals&filter_value_1=Polimedia"><img src="<%= request.getContextPath() %>/image/polimedia.png" alt=""><span>Polimedias</span></a></li>
          <li><a class="clearfix" href="<%= request.getContextPath() %>/simple-search?location=123456789%2F&query=&rpp=10&sort_by=score&order=desc"><img src="<%= request.getContextPath() %>/image/otros.png" alt=""><span>Otras Colecciones</span></a></li>

      </ul>
      </div>
      </div>
</div>
<br/>
                <%-- Location bar --%>
<%
    if (locbar)
    {
%>
<div class="container">
                <dspace:include page="/layout/location-bar.jsp" />
</div>                
<%
    }
%>


        <%-- Page contents --%>
<div class="container">
<% if (request.getAttribute("dspace.layout.sidebar") != null) { %>
	<div class="row">
		<div class="col-md-9">
<% } %>		
