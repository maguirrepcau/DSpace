<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Footer for home page
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>

<%
    String sidebar = (String) request.getAttribute("dspace.layout.sidebar");
%>

            <%-- Right-hand side bar if appropriate --%>
<%
    if (sidebar != null)
    {
%>
	</div>
	<div class="col-md-3">
                    <%= sidebar %>
    </div>
    </div>       
<%
    }
%>
</div>
</main>
    <%-- Page footer --%>
       <footer  style="background:#313131" >
			 <div  class="container text-muted" >

			
			<%-- Columna1 --%>
			<div  class="col-md-3">                                    
            <div >
				<h4 >Nuestros sitios web</h4>
				<div id="link-footer">
				<ul>
				<li><a class="clearfix" href="http://www.instituto.continental.edu.pe" target="_blank">Instituto Continental</a></li>

				<li><a class="clearfix" href="http://www.universidad.continental.edu.pe/" target="_blank">Universidad Continental</a></li>
				<li><a class="clearfix" href="http://postgrado.continental.edu.pe" target="_blank">Escuela de Postgrado</a></li>
				<li><a class="clearfix" href="http://virtual.ucontinental.edu.pe/" target="_blank">Modalidad Virtual</a></li>
				<li><a class="clearfix" href="http://cec.ucontinental.edu.pe/" target="_blank">Educación Continua</a></li>
				<li><a class="clearfix" href="http://gentequetrabaja.ucontinental.edu.pe/" target="_blank">Modalidad Gente que Trabaja</a></li>
				</ul>
				</div>
			</div>
			</div>
			
			<%-- Columna2 --%>
			<div  class="col-md-3"> 
				<h4>Contacto:</h4>	
				<div id="link-footer">
						
				<div ><p><strong>Lima</strong><br />
				Jr. Junin 355, Miraflores<br />
				Teléfonos: 01-2132760<br />
				<br />
				<strong>Huancayo</strong><br />
				Av. San Carlos 1980 - Huancayo<br />
				Teléfonos: (064) 481430</p>
				</div>
				</div>
				<div>
				<br><br>
				<img src="<%= request.getContextPath() %>/image/derecho.png" alt="CC"><br>
						<span>Todos los contenidos de repositorio.continental.edu.pe, est&aacute;n licenciados bajo</span><br> 
						<a style="text-decoration:none; color:#6699CC;" href="http://creativecommons.org/licenses/by-nc/3.0/"><u>Creative Commons License</u></a>

				</div>
            </div>
			
		<%-- Columna3 --%>
			<div  class="col-md-3">
			<h4>   Escríbenos:</h4>	
				<script type="text/javascript" src="https://form.jotformz.com/jsform/53226646251655"></script>
            </div>

            
			<%-- Columna4 --%>
			<div  class="col-md-3">                                    
                   <div>
				<h4>Sitios relacionados</h4>
				<div id="link-footer">
				<ul>
				<li><a class="clearfix" href="http://cendoc.continental.edu.pe/" target="_blank">CENDOC</a></li>
				<li><a class="clearfix" href="http://fondoeditorial.continental.edu.pe/" target="_blank">Fondo Editorial </a></li>
				<li><a class="clearfix" href="http://www.universidad.continental.edu.pe/instituto-investigacion/" target="_blank">Unidad de Investigación </a></li>
				<li><a class="clearfix" href="http://www.universidad.continental.edu.pe/recursos-aprendizaje" target="_blank">Recursos de Aprendizaje </a></li>
				<li><a class="clearfix" href="http://blog.continental.edu.pe/centro-cultural/" target="_blank">Centro Cultural </a></li>
				</ul>
				</div>
			</div>       
            </div>
			</div>
    </footer>
    </body>
</html>