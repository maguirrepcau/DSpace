/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authenticate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory; 
import com.google.api.client.json.JsonFactory;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import static org.dspace.authenticate.AuthenticationMethod.BAD_ARGS;
import static org.dspace.authenticate.AuthenticationMethod.BAD_CREDENTIALS;
import static org.dspace.authenticate.AuthenticationMethod.CERT_REQUIRED;
import static org.dspace.authenticate.AuthenticationMethod.NO_SUCH_USER;
import static org.dspace.authenticate.AuthenticationMethod.SUCCESS;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 *
 * @author maguirrep
 */
public class OAuthAuthentication 
    implements AuthenticationMethod {

    /** log4j category */
    private static Logger log = Logger.getLogger(OAuthAuthentication.class);

    /**
     * Let a real auth method return true if it wants.
     */
    public boolean canSelfRegister(Context context,
                                   HttpServletRequest request,
                                   String username)
        throws SQLException
    {
        // Looks to see if autoregister is set or not
        return ConfigurationManager.getBooleanProperty("authentication-oauth", "autoregister");
    }

    /**
     *  Nothing here, initialization is done when auto-registering.
     */
    public void initEPerson(Context context, HttpServletRequest request,
            EPerson eperson)
        throws SQLException
    {
        // XXX should we try to initialize netid based on email addr,
        // XXX  for eperson created by some other method??
    }

    /**
     * Cannot change LDAP password through dspace, right?
     */
    public boolean allowSetPassword(Context context,
                                    HttpServletRequest request,
                                    String username)
        throws SQLException
    {
        // XXX is this right?
        return false;
    }
    /*
     * This is an explicit method.
     */
    public boolean isImplicit()
    {
        return false;
    }

    /*
     * Add authenticated users to the group defined in dspace.cfg by
     * the login.specialgroup key.
     */
    public int[] getSpecialGroups(Context context, HttpServletRequest request)
    {
        // Prevents anonymous users from being added to this group, and the second check
        // ensures they are LDAP users
        try
        {
            if (!context.getCurrentUser().getNetid().equals(""))
            {
                String groupName = ConfigurationManager.getProperty("authentication-ldap", "login.specialgroup");
                if ((groupName != null) && (!groupName.trim().equals("")))
                {
                    Group ldapGroup = Group.findByName(context, groupName);
                    if (ldapGroup == null)
                    {
                        // Oops - the group isn't there.
                        log.warn(LogManager.getHeader(context,
                                "ldap_specialgroup",
                                "Group defined in login.specialgroup does not exist"));
                        return new int[0];
                    } else
                    {
                        return new int[] { ldapGroup.getID() };
                    }
                }
            }
        }
        catch (Exception npe) {
            // The user is not an LDAP user, so we don't need to worry about them
        }
        return new int[0];
    }

    /*
     * Authenticate the given credentials.
     * This is the heart of the authentication method: test the
     * credentials for authenticity, and if accepted, attempt to match
     * (or optionally, create) an <code>EPerson</code>.  If an <code>EPerson</code> is found it is
     * set in the <code>Context</code> that was passed.
     *
     * @param context
     *  DSpace context, will be modified (ePerson set) upon success.
     *
     * @param username
     *  Username (or email address) when method is explicit. Use null for
     *  implicit method.
     *
     * @param password
     *  Password for explicit auth, or null for implicit method.
     *
     * @param realm
     *  Realm is an extra parameter used by some authentication methods, leave null if
     *  not applicable.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @return One of:
     *   SUCCESS, BAD_CREDENTIALS, CERT_REQUIRED, NO_SUCH_USER, BAD_ARGS
     * <p>Meaning:
     * <br>SUCCESS         - authenticated OK.
     * <br>BAD_CREDENTIALS - user exists, but credentials (e.g. passwd) don't match
     * <br>CERT_REQUIRED   - not allowed to login this way without X.509 cert.
     * <br>NO_SUCH_USER    - user not found using this method.
     * <br>BAD_ARGS        - user/pw not appropriate for this method
     */
    public int authenticate(Context context,
                            String netid,//idTokenString
                            String password,
                            String realm,
                            HttpServletRequest request)
        throws SQLException 
    {
        //System.out.println("Esta entrando OauthAuthentication");
        //System.out.println(netid);
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance(); 
        NetHttpTransport transport = new NetHttpTransport();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport,jsonFactory)
        .setAudience(Arrays.asList("1031980969450-g5sni4ge7vi4ck6mmnsba29iqqacb50q.apps.googleusercontent.com"))
        // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
        // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
        // "accounts.google.com". If you need to verify tokens from multiple sources, build
        // a GoogleIdTokenVerifier for each issuer and try them both.
        .build();
        
        GoogleIdToken idToken = null; 
          try { 
           idToken = verifier.verify(netid); 
          }
          catch(Exception e) { 
           e.printStackTrace(); 
          } 

          
          String email=null;
          String name = null;
          String pictureUrl = null;
          String locale = null;
          String familyName = null;
          String givenName = null;
          
          if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();

            // Get profile information from payload
            email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            name = (String) payload.get("name");
            pictureUrl = (String) payload.get("picture");
            locale = (String) payload.get("locale");
            familyName = (String) payload.get("family_name");
            givenName = (String) payload.get("given_name");

          } 
          else 
          {
            System.out.println("Invalid ID token.");
          }
         
        String pattern = "(.*)(@)(.*)";
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(email);
        if (m.find( ))
        {
          if(!m.group(3).equalsIgnoreCase("continental.edu.pe"))
          {
              return BAD_ARGS;
          }
        } else {
           System.out.println("NO MATCH");
           return BAD_ARGS;
        }
        //==================================================================================================================================
        log.info(LogManager.getHeader(context, "auth", "attempting trivial auth of user="+email));

        
        // Skip out when no netid or password is given.
        if (email == null)
        {
            return BAD_ARGS;
        }

        // Locate the eperson
        EPerson eperson = null;
        try
        {
                eperson = EPerson.findByEmail(context, email);
        }
        catch (SQLException e)
        {
        }
        catch (AuthorizeException e)
        {
        }
        

        // Get the DN of the user
        //boolean anonymousSearch = ConfigurationManager.getBooleanProperty("authentication-ldap", "search.anonymous");
        //String adminUser = ConfigurationManager.getProperty("authentication-ldap", "search.user");
        //String adminPassword = ConfigurationManager.getProperty("authentication-ldap", "search.password");
        //String objectContext = ConfigurationManager.getProperty("authentication-ldap", "object_context");
        //String idField = ConfigurationManager.getProperty("authentication-ldap", "id_field");
        //String dn = "";

        // If adminUser is blank and anonymous search is not allowed, then we can't search so construct the DN instead of searching it
        //if ((StringUtils.isBlank(adminUser) || StringUtils.isBlank(adminPassword)) && !anonymousSearch)
        //{
        //    dn = idField + "=" + netid + "," + objectContext;
        //}
        //else
        //{
        //    dn = ldap.getDNOfUser(adminUser, adminPassword, context, netid);
        //}

        // Check a DN was found
        //if ((dn == null) || (dn.trim().equals("")))
        //{
        //    log.info(LogManager
        //        .getHeader(context, "failed_login", "no DN found for user " + netid));
        //    return BAD_CREDENTIALS;
        //}

        // if they entered a netid that matches an eperson
        if (eperson != null)
        {
            System.out.println("L292 eperson se encontro: ");
            // e-mail address corresponds to active account
            if (eperson.getRequireCertificate())
            {
                return CERT_REQUIRED;
            }
            else if (!eperson.canLogIn())
            {
                return BAD_ARGS;
            }

            //if (ldap.ldapAuthenticate(dn, password, context))
            //{
            if(email!=null)
            {
                context.setCurrentUser(eperson);

                // assign user to groups based on ldap dn
            //    assignGroups(dn, ldap.ldapGroup, context);
                
                log.info(LogManager
                    .getHeader(context, "authenticate", "type=oauth"));
                System.out.println("llego hasta el succes: ");
                return SUCCESS;
            }
            else
            {
                return BAD_CREDENTIALS;
            }
        }
        else
        {
            //System.out.println("L323 eperson es null");
            // the user does not already exist so try and authenticate them
            // with ldap and create an eperson for them

            //if (ldap.ldapAuthenticate(dn, password, context))
            if(email!=null)
            {
                // Register the new user automatically
                //log.info(LogManager.getHeader(context,
                //                "autoregister", "netid=" + netid));

                //String email = ldap.ldapEmail;

                // Check if we were able to determine an email address from LDAP
                if (StringUtils.isEmpty(email))
                {
                    // If no email, check if we have a "netid_email_domain". If so, append it to the netid to create email
                    //if (StringUtils.isNotEmpty(ConfigurationManager.getProperty("authentication-ldap", "netid_email_domain")))
                    //{
                    //    email = netid + ConfigurationManager.getProperty("authentication-ldap", "netid_email_domain");
                    //}
                    //else
                    //{
                        // We don't have a valid email address. We'll default it to 'netid' but log a warning
                    //    log.warn(LogManager.getHeader(context, "autoregister",
                    //            "Unable to locate email address for account '" + netid + "', so it has been set to '" + netid + "'. " +
                    //            "Please check the LDAP 'email_field' OR consider configuring 'netid_email_domain'."));
                    //    email = netid;
                    //}
                }

                if (StringUtils.isNotEmpty(email))
                {
                    try
                    {
                        eperson = EPerson.findByEmail(context, email);
                        if (eperson!=null)
                        {
                            // System.out.println("L361 eperson se encontro");
                            //log.info(LogManager.getHeader(context,
                            //        "type=oauth-login", "type=oauth_but_already_email"));
                            //context.setIgnoreAuthorization(true);
                            //eperson.setNetid(netid.toLowerCase());
                            //eperson.update();
                            //context.commit();
                            context.setIgnoreAuthorization(false);
                            context.setCurrentUser(eperson);

                            // assign user to groups based on ldap dn
                            //assignGroups(dn, ldap.ldapGroup, context);

                            //return SUCCESS;
                        }
                        else
                        {
                            //System.out.println("L378 eperson no se encontro, se creara");
                            if (canSelfRegister(context, request, netid))
                            {
                                //System.out.println("Se puede crear "+canSelfRegister(context, request, netid));
                                // TEMPORARILY turn off authorisation
                                try
                                {
                                    context.setIgnoreAuthorization(true);
                                    eperson = EPerson.create(context);
                                    if (StringUtils.isNotEmpty(email))
                                    {
                                        eperson.setEmail(email);
                                        //System.out.println("creando email "+eperson.getEmail());
                                    }
                                    if (StringUtils.isNotEmpty(givenName))
                                    {
                                        eperson.setFirstName(givenName);
                                        //System.out.println("creando first name "+eperson.getFirstName());
                                    }
                                    if (StringUtils.isNotEmpty(familyName))
                                    {
                                        eperson.setLastName(familyName);
                                        //System.out.println("creando first lastname "+eperson.getLastName());
                                    }
                                   // if (StringUtils.isNotEmpty(ldap.ldapPhone))                                    
                                   // {
                                   //     eperson.setMetadata("phone", ldap.ldapPhone);
                                   // }
                                   //eperson.setNetid(netid.toLowerCase());
                                    eperson.setCanLogIn(true);
                                    //System.out.println("creando can loggin " + eperson.canLogIn());
                                    AuthenticationManager.initEPerson(context, request, eperson);
                                    eperson.update();
                                    context.commit();
                                    //System.out.println("se creo "+ eperson );
                                    context.setCurrentUser(eperson);
                                    //System.out.println("context.setCurrentUser " + context.getCurrentUser());
                                    // assign user to groups based on ldap dn
                                    //assignGroups(dn, ldap.ldapGroup, context);
                                }
                                catch (AuthorizeException e)
                                {
                                    return NO_SUCH_USER;
                                }
                                finally
                                {
                                    context.setIgnoreAuthorization(false);
                                }

                                log.info(LogManager.getHeader(context, "authenticate",
                                            "type=oauth-login, created ePerson"));
                                return SUCCESS;
                            }
                            else
                            {
                                // No auto-registration for valid certs
                                log.info(LogManager.getHeader(context,
                                                "failed_login", "type=oauth_but_no_record"));
                                return NO_SUCH_USER;
                            }
                        }
                    }
                    catch (AuthorizeException e)
                    {
                        eperson = null;
                    }
                    finally
                    {
                        context.setIgnoreAuthorization(false);
                    }
                }
            }
        }
        return BAD_ARGS;
    }

    /**
     * Internal class to manage LDAP query and results, mainly
     * because there are multiple values to return.
     */
    private static class SpeakerToLDAP {

        private Logger log = null;

        protected String ldapEmail = null;
        protected String ldapGivenName = null;
        protected String ldapSurname = null;
        protected String ldapPhone = null;
        protected String ldapGroup = null;

        /** LDAP settings */
        String ldap_provider_url = ConfigurationManager.getProperty("authentication-ldap", "provider_url");
        String ldap_id_field = ConfigurationManager.getProperty("authentication-ldap", "id_field");
        String ldap_search_context = ConfigurationManager.getProperty("authentication-ldap", "search_context");
        String ldap_search_scope = ConfigurationManager.getProperty("authentication-ldap", "search_scope");

        String ldap_email_field = ConfigurationManager.getProperty("authentication-ldap", "email_field");
        String ldap_givenname_field = ConfigurationManager.getProperty("authentication-ldap", "givenname_field");
        String ldap_surname_field = ConfigurationManager.getProperty("authentication-ldap", "surname_field");
        String ldap_phone_field = ConfigurationManager.getProperty("authentication-ldap", "phone_field");
        String ldap_group_field = ConfigurationManager.getProperty("authentication-ldap", "login.groupmap.attribute"); 

        SpeakerToLDAP(Logger thelog)
        {
            log = thelog;
        }

        protected String getDNOfUser(String adminUser, String adminPassword, Context context, String netid)
        {
            // The resultant DN
            String resultDN;

            // The search scope to use (default to 0)
            int ldap_search_scope_value = 0;
            try
            {
                ldap_search_scope_value = Integer.parseInt(ldap_search_scope.trim());
            }
            catch (NumberFormatException e)
            {
                // Log the error if it has been set but is invalid
                if (ldap_search_scope != null)
                {
                    log.warn(LogManager.getHeader(context,
                            "ldap_authentication", "invalid search scope: " + ldap_search_scope));
                }
            }

            // Set up environment for creating initial context
            Hashtable env = new Hashtable(11);
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(javax.naming.Context.PROVIDER_URL, ldap_provider_url);

            if ((adminUser != null) && (!adminUser.trim().equals("")) &&
                (adminPassword != null) && (!adminPassword.trim().equals("")))
            {
                // Use admin credentials for search// Authenticate
                env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
                env.put(javax.naming.Context.SECURITY_PRINCIPAL, adminUser);
                env.put(javax.naming.Context.SECURITY_CREDENTIALS, adminPassword);
            }
            else
            {
                // Use anonymous authentication
                env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
            }

            DirContext ctx = null;
            try
            {
                // Create initial context
                ctx = new InitialDirContext(env);

                Attributes matchAttrs = new BasicAttributes(true);
                matchAttrs.put(new BasicAttribute(ldap_id_field, netid));

                // look up attributes
                try
                {
                    SearchControls ctrls = new SearchControls();
                    ctrls.setSearchScope(ldap_search_scope_value);

                    NamingEnumeration<SearchResult> answer = ctx.search(
                            ldap_provider_url + ldap_search_context,
                            "(&({0}={1}))", new Object[] { ldap_id_field,
                                    netid }, ctrls);

                    while (answer.hasMoreElements()) {
                        SearchResult sr = answer.next();
                        if (StringUtils.isEmpty(ldap_search_context)) {
                            resultDN = sr.getName();
                        } else {
                            resultDN = (sr.getName() + "," + ldap_search_context);
                        }

                        String attlist[] = {ldap_email_field, ldap_givenname_field,
                                            ldap_surname_field, ldap_phone_field, ldap_group_field};
                        Attributes atts = sr.getAttributes();
                        Attribute att;

                        if (attlist[0] != null) {
                            att = atts.get(attlist[0]);
                            if (att != null)
                            {
                                ldapEmail = (String) att.get();
                            }
                        }

                        if (attlist[1] != null) {
                            att = atts.get(attlist[1]);
                            if (att != null)
                            {
                                ldapGivenName = (String) att.get();
                            }
                        }

                        if (attlist[2] != null) {
                            att = atts.get(attlist[2]);
                            if (att != null)
                            {
                                ldapSurname = (String) att.get();
                            }
                        }

                        if (attlist[3] != null) {
                            att = atts.get(attlist[3]);
                            if (att != null)
                            {
                                ldapPhone = (String) att.get();
                            }
                        }
                
                        if (attlist[4] != null) {
                            att = atts.get(attlist[4]);
                            if (att != null) 
                            {
                                ldapGroup = (String) att.get();
                            }
                        }

                        if (answer.hasMoreElements()) {
                            // Oh dear - more than one match
                            // Ambiguous user, can't continue

                        } else {
                            log.debug(LogManager.getHeader(context, "got DN", resultDN));
                            return resultDN;
                        }
                    }
                }
                catch (NamingException e)
                {
                    // if the lookup fails go ahead and create a new record for them because the authentication
                    // succeeded
                    log.warn(LogManager.getHeader(context,
                                "ldap_attribute_lookup", "type=failed_search "
                                        + e));
                }
            }
            catch (NamingException e)
            {
                log.warn(LogManager.getHeader(context,
                            "ldap_authentication", "type=failed_auth " + e));
            }
            finally
            {
                // Close the context when we're done
                try
                {
                    if (ctx != null)
                    {
                        ctx.close();
                    }
                }
                catch (NamingException e)
                {
                }
            }

            // No DN match found
            return null;
        }

        /**
         * contact the ldap server and attempt to authenticate
         */
        protected boolean ldapAuthenticate(String netid, String password,
                        Context context) {
            if (!password.equals("")) {
                // Set up environment for creating initial context
                Hashtable<String, String> env = new Hashtable<String, String>();
                env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                        "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(javax.naming.Context.PROVIDER_URL, ldap_provider_url);

                // Authenticate
                env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "Simple");
                env.put(javax.naming.Context.SECURITY_PRINCIPAL, netid);
                env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);
                env.put(javax.naming.Context.AUTHORITATIVE, "true");
                env.put(javax.naming.Context.REFERRAL, "follow");

                DirContext ctx = null;
                try {
                    // Try to bind
                    ctx = new InitialDirContext(env);
                } catch (NamingException e) {
                    log.warn(LogManager.getHeader(context,
                            "ldap_authentication", "type=failed_auth " + e));
                    return false;
                } finally {
                    // Close the context when we're done
                    try {
                        if (ctx != null)
                        {
                            ctx.close();
                        }
                    } catch (NamingException e) {
                    }
                }
            } else {
                return false;
            }

            return true;
        }        
    }

    /*
     * Returns URL to which to redirect to obtain credentials (either password
     * prompt or e.g. HTTPS port for client cert.); null means no redirect.
     *
     * @param context
     *  DSpace context, will be modified (ePerson set) upon success.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @param response
     *  The HTTP response from the servlet method.
     *
     * @return fully-qualified URL
     */
    public String loginPageURL(Context context,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        return response.encodeRedirectURL(request.getContextPath() +
                                          "/ldap-login");
    }

    /**
     * Returns message key for title of the "login" page, to use
     * in a menu showing the choice of multiple login methods.
     *
     * @param context
     *  DSpace context, will be modified (ePerson set) upon success.
     *
     * @return Message key to look up in i18n message catalog.
     */
    public String loginPageTitle(Context context)
    {
        return "org.dspace.eperson.LDAPAuthentication.title";
    }


    /*
     * Add authenticated users to the group defined in dspace.cfg by
     * the authentication-ldap.login.groupmap.* key.
     */
    private void assignGroups(String dn, String group, Context context)
    {
        if (StringUtils.isNotBlank(dn)) 
        {
            System.out.println("dn:" + dn);
            int i = 1;
            String groupMap = ConfigurationManager.getProperty("authentication-ldap", "login.groupmap." + i);
            
            boolean cmp;
            
            while (groupMap != null)
            {
                String t[] = groupMap.split(":");
                String ldapSearchString = t[0];
                String dspaceGroupName = t[1];
 
                if (group == null) {
                    cmp = StringUtils.containsIgnoreCase(dn, ldapSearchString + ",");
                } else {
                    cmp = StringUtils.equalsIgnoreCase(group, ldapSearchString);
                }

                if (cmp) 
                {
                    // assign user to this group   
                    try
                    {
                        Group ldapGroup = Group.findByName(context, dspaceGroupName);
                        if (ldapGroup != null)
                        {
                            ldapGroup.addMember(context.getCurrentUser());
                            ldapGroup.update();
                            context.commit();
                        }
                        else
                        {
                            // The group does not exist
                            log.warn(LogManager.getHeader(context,
                                    "ldap_assignGroupsBasedOnLdapDn",
                                    "Group defined in authentication-ldap.login.groupmap." + i + " does not exist :: " + dspaceGroupName));
                        }
                    }
                    catch (AuthorizeException ae)
                    {
                        log.debug(LogManager.getHeader(context, "assignGroupsBasedOnLdapDn could not authorize addition to group", dspaceGroupName));
                    }
                    catch (SQLException e)
                    {
                        log.debug(LogManager.getHeader(context, "assignGroupsBasedOnLdapDn could not find group", dspaceGroupName));
                    }
                }

                groupMap = ConfigurationManager.getProperty("authentication-ldap", "login.groupmap." + ++i);
            }
        }
    }
}