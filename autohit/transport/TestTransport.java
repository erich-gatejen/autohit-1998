/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

/**
 *  A test transport.  Used to test the system
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 25Jan99</code> 
 * 
 */
public class TestTransport implements Transport {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------
    

	// --- PUBLIC METHODS ----------------------------------------------------
	
    /**
     *  Default constructor.
     */
    public TestTransport() {

    }
     
    /**
     *  Connect    
     */     
    public void connect(String  address) throws TransportException {
        
        System.out.println("TEST TRANSPORT: Address=" + address);
    }

    /**
     *  Push a query and <b>wait</b> for a response.
     */   
    public Response push(Query  q) throws TransportException {

        System.out.println("TEST TRANSPORT: push qs=" + q.qs);
       
        Response r = new Response();
        r.headers  = null;
        r.code     = 200;
        r.content  = null;
        r.cLength  = 0;

        return r;
    }

    /**
     *  Set an environment variable for this transport.
     */ 
    public void environment(String  name, String  value) {

        System.out.println("TEST TRANSPORT: environment.  name=" + name + " value =" + value);
    }

    /**
     *  Disconnect transport.
     */
    public void disconnect() {
          
    }
 
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
