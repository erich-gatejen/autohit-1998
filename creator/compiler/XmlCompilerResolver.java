/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package creator.compiler;

//import org.xml.sax.EntityResolver; 
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.StringReader;
import java.util.Vector;
import com.sun.xml.parser.Resolver;

/**
 * Implement our own resolver to handle XML activities.  
 * For the most part, this is used to provide DTDs.
 * 
 * Do NOT use the default constructor!
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 12Jan99</code> 
 * 
 */
public class XmlCompilerResolver extends Resolver {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  The DTD to use when parsing the Sim text.
     */      	
    private String     myDTD;  

    /**
     *  The entity to trap.
     */      	
    private String     myURI;  
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.  Set up the input source.  We will pass to this
     * a string containing the DTD.
     *
     *  @param uri A string containing the textual entity to trap and resolve.
     *  @param dtdText A string containing the DTD.
     */
     XmlCompilerResolver(String uri, String   dtdText) {
 
        super();
        
        myDTD       = dtdText;
        myURI       = uri;
    }

    /**
     *  overrides the resolver.
     *
     *  @param name not implimented.
     *  @param uri Passed to this from the parser.  We will trap
     *                  the uri.
     *  @return an input source to be used by the XML parser.
     */
    public InputSource resolveEntity (java.lang.String name, 
                                      java.lang.String uri)   
                                     throws SAXException, java.io.IOException {

//DEBUG
//System.out.println("Resolve Entity: public=[" + name + "] system=[" + uri + "]");


            // Is it asking for the SIM.DTD?
            if (uri.equals(myURI)) {
                
                // return a reader for the dtdText
                return new InputSource(new StringReader(myDTD));
                
            } else {

                // use the default behaviour
                return new InputSource(new StringReader(myDTD));            
            
            }

    }

                         
    
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
