/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

import HTTPClient.NVPair;

/**
 * A standard response to a Query.
 *
 * We are going to "borrow" a class from the HTTPClient lib--NVPair.
 * However, this Query class is *NOT* specific to http transports.
 *
 * Some transport will allow more than one Response per Query.  They can be
 * chained via the 'next' field.  
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99
 * EPG - Allow response chaining - 5Feb99</code> 
 * 
 */
public class Response {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * Headers.  
     */      	
    public NVPair[]    headers;

    /**
     * Response code.
     */      	
    public int         code;
    
    /**
     * Content data.
     */      	
    public byte[]      content;
    
    /**
     * Content length.
     */      	
    public int         cLength;
    
    /**
     * Next response.
     */      	
    public Response    next;        

	// --- PUBLIC METHODS ----------------------------------------------------
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
