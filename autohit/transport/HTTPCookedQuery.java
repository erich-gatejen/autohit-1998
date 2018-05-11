/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

import HTTPClient.NVPair;

/**
 * A cooked query for HTTP client.  I want to get rid of this some day, but
 * for now we have to do some translations for HTTPClient.
 *
 * @author Erich P. Gatejen
 * @version 1.1
 * <i>Version History</i>
 * <code>EPG - Initial - 8Mar99</code> 
 * 
 */
public class HTTPCookedQuery {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * Headers.  
     */      	
    public NVPair[]    headers;

    /**
     * Body elements.
     */      	
    public NVPair[]    body;
    
    
	// --- PUBLIC METHODS ----------------------------------------------------
 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
