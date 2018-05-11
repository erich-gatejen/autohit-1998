/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

/**
 * An interface to a standard query/response transport facillity.
 * <p>
 * this may seem like an unneccessary layer of abstraction, but I have
 * plans for the future...
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99</code> 
 * 
 */
public interface Transport {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------
	
    /**
     *  Open the transport to the given address.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.TransportException     
     */     
    public void connect(String  address) throws TransportException;

    /**
     *  Push a query and wait for a response.
     *  
     *  @param q A queury specification.
     *  @return A response object.
     *  @throws autohit.transport.TransportException
     *  @see autohit.transport.Query
     *  @see autohit.transport.Response
     */   
    public Response push(Query  q) throws TransportException;

    /**
     *  Set an environment variable for this transport.
     *  
     *  @param name variable name.
     *  @param value variable value.
     */ 
    public void environment(String  name, String  value);

    /**
     *  Disconnet transport.
     *  
     */
    public void disconnect();	
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
