/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

import HTTPClient.NVPair;

/**
 * A standard query for the transport facillity.
 * <p>
 * We are going to "borrow" a class from the HTTPClient lib--NVPair.
 * However, this Query class is *NOT* specific to http transports.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99</code> 
 * 
 */
public class Query {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * Headers.  
     */      	
    public NVPair[]    headers;

    /**
     * Query String.
     */      	
    public String      qs;
    
    /**
     * Body elements.
     */      	
    public NVPair[]    body;
    
    
	// --- PUBLIC METHODS ----------------------------------------------------
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
