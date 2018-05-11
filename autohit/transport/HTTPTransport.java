/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.CookiePolicyHandler;
import HTTPClient.CookieModule;
import HTTPClient.Cookie;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;

/**
 *  A HTTP transport.  It uses the HTTPClient library.  for http implementation specifics, 
 *  see the library documentation.
 *  <p>
 *  I'm thinking that HTTPClient will have to get replaced.  It will be slow.  It
 *  works for multi-client simulation, but it isn't designed for it.  I'm worried
 *  that Objects and cookies will ghost and build up.
 *  <p>
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99</code> 
 * 
 */
public class HTTPTransport implements Transport, CookiePolicyHandler {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Address.
     */ 
    private String  addr;

    /**
     *  Port.
     */ 
    private int     port;
    
    /**
     *  HTTP Connection.
     */ 
    private HTTPConnection  con;
  
    /*
     *  Allow cookies?
     */ 
    private boolean cookies     = true;
    

	// --- PUBLIC METHODS ----------------------------------------------------
	
    /**
     *  Default constructor.
     */
    public HTTPTransport() {
        
        con = null;        
    }
     
    /**
     *  Prepare to connect to a web server.  If this method is subsiquently
     *  called BEFORE the connection is disconnect()'ed, it will throw a TransportException.
     * 
     *  The port can be specified in the address--such as "my.domain.com:8080"
     *
     *  @param address Address specification.  This should only be the DOMAIN portion
     *                 of a URL.
     *
     *  @throws autohit.transport.TransportException     
     */     
    public void connect(String  address) throws TransportException {
        
        if (con != null) throw new TransportException("Already connected.");

        try {
            // is there a port or not?
            int c = address.indexOf(":");
            if (c >= 0) {
                addr = address.substring(0, c-1);
                String portText = address.substring(c+1, address.length()-1 );
                port = Integer.parseInt(portText);
                
            } else {
                addr = address;
                port = 80;           
            }
            
        } catch (Exception e) {  throw new TransportException("Bad address string."); }
        
        con = new HTTPConnection(addr, port);
        con.setContext(this);                   // We want to be autonomous.
        CookieModule.setCookiePolicyHandler(this);  // move this? 
    }

    /**
     *  Push a query and <b>wait</b> for a response.
     *  <p>  
     *  <pre>
     *  The Query should be formed as the following:
     *      Query.headers = any non-default headers.  Be careful that they they
     *                      don't break how HTTPClient works.
     *      Query.qs      = The URI query string including URL encoded variables.
     *                      It should include the "/" after the domain in URI--such
     *                      as "/cgi-bin/goats.pl" rather than "cgi-bin/goats.pl"
     *      Query.body    = Any body elements.  If this is null, then the http GET method
     *                      will be used.  Otherwise, http POST is used and this body will
     *                      be the form data.
     *  </pre>
     *  <p>
     *  WARNING!  For now, headers are not given in the Response.  Reponse.header
     *  will be null.
     *  <p>
     *  If it is not connected or there is an underlying transport error, it will 
     *  throw a TransportException.
     *
     *  @param q A queury specification.
     *  @return A response object.
     *  @throws autohit.transport.TransportException
     *  @see autohit.transport.Query
     *  @see autohit.transport.Response
     */   
    public Response push(Query  q) throws TransportException {

        if (addr == null) throw new TransportException("Not connected.");

        HTTPResponse    rep;
        Response        r; 
 
        try {

            if (q.body == null) {
            
                // USE GET
                rep = con.Get(q.qs, "", q.headers);
            
            } else {     
                      
                // USE POST
                rep = con.Post(q.qs, q.body, q.headers);
                       
            }
        } catch (Exception e) {
            throw new TransportException("Failed Query [" + addr + ":" + port + q.qs + "] with JAVA Exception: " + e.getMessage() );
        }

        try {        
            r = new Response();
            r.headers  = null;
            r.code     = rep.getStatusCode();
            r.content  = rep.getData();
            r.cLength  = r.content.length;
        } catch (Exception e) {
            throw new TransportException("Failed Response [" + addr + ":" + port + q.qs + "] with JAVA Exception: " + e.getMessage() );
        }

        return r;
    }

    /**
     *  Set an environment variable for this transport.
     *  <p><pre>
     *  The following as defined for this transport.  (Defaults shown in paren.)
     *
     *      - Accept cookies?   "cookies" == "true" or "false" (true)
     *      - Allow redirects?  "redir" == "true" or "false" (true)
     *                          WARNING!  For now, redirects will ALWAYS be allowed.
     *                          I'll have to hack the HTTPClient to change this....
     *  </pre><p>
     *  Names and boolean values are NOT case sensitive.
     *  
     *  @param name variable name.
     *  @param value variable value.
     */ 
    public void environment(String  name, String  value) {

        if (name.compareToIgnoreCase("cookies") == 0) {
            
            if (value.compareToIgnoreCase("true") == 0) cookies = true;
            else cookies = false;           
            
        } //else if (name.compareToIgnoreCase("redir") == 0) {
        
        //}
    }

    /**
     *  Disconnect transport.
     *  <p>
     *  If it isn't currently connected, nothing bad will happen.
     */
    public void disconnect() {
        
        // Null this reference.  Hopefully GC will get to it soon.  :D
        // ...  erk, I also hope the cookies all go away when there are no
        // more active HTTPConnection's.
        con = null;            
    }
 
	// --- COOKIE INTERFACE ---------------------------------------------------

    /**
     *  Just don't use these...  We will use the environemnt var to decide whether
     *  to allow cookies....
     */       
    public boolean acceptCookie(Cookie      cookie,
                                RoRequest   req,
                                RoResponse  resp) {
        return cookies;                                    
                                        
    }
    public boolean sendCookie(Cookie cookie, RoRequest req) {
                                    
        return cookies;
    }                                
                                    
    
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
