/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package creator.compiler;

import java.util.Vector;
import org.xml.sax.ErrorHandler; 
import org.xml.sax.HandlerBase;         // <-- remove??
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Parse Error Handler.  It will log and present errors and warnings
 * for a compilation.  It is also the error handler for the XML
 * parser.
 *
 * WARNING!!!  An instance of this must be registered with the parser
 * before EACH compile.
 *
 *
 * @see autohit.Sim
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 13Jan99</code> 
 * 
 */
public class XmlParseErrorHandler implements ErrorHandler {

	// --- FINAL FIELDS ---------------------------------------------

	// --- FIELDS ------------------------------------------------------------

    /**
     *  A vector containing error strings.
     */ 
    public Vector errors;

    /**
     *  A vector containing warning strings.
     */ 
    public Vector warnings;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.
     *
     */
    public XmlParseErrorHandler()  {
    
        super();    // Call constructor for HandlerBase()
        
        errors   = new Vector();      
        warnings = new Vector();        
    }
  
    /**
     *  Add an error to the error log.
     */
    public void error (String  text) {
        errors.add(text);
    }
    

    /**
     *  Pretty print the errors and warnings.
     *
     *  @return a string containing the print.
     */
    public String prettyPrint() {

        StringBuffer  p = new StringBuffer();
        
        // do errors
        p.append("Errors ---------------------------------------\n");
        for (int eidx = 0; eidx < errors.size(); eidx++) {
            p.append(errors.get(eidx));
            p.append("\n");
        }
        // do warnings
        p.append("Warnings ------------------------------------\n");
        for (int eidx = 0; eidx < warnings.size(); eidx++) {
            p.append(warnings.get(eidx));
            p.append("\n");
        }

        return p.toString();        
    }


    
	// --- PRIVATE METHODS ---------------------------------------------------	


	// --- INTERNAL METHODS ---------------------------------------------------	

    /**
     *  Receive an error from the compiler.  Do not call this method directly.
     *  
     *  @throws SAXException Send back to document builder
     */
    public void error (SAXParseException e) throws SAXException {
    
        String entry = "ParseError @ line " + e.getLineNumber() + " : " + e.getMessage();
        errors.add(entry);
        
        //throw e;  // Do I want to can these?
    }

    /**
     *  Receive a fatal error from the compiler.  Do not call this method directly.
     *  
     *  @throws SAXException Send back to document builder
     */
    public void fatalError (SAXParseException e) throws SAXException {
    
        String entry = "FATAL ParseError @ line " + e.getLineNumber() + " : " + e.getMessage();
        errors.add(entry);
        
        //throw e;  // Do I want to can these?
    }


    /**
     *  Receive a warning from the compiler.  Do not call this method directly.
     *  
     *  @throws SAXException Send back to document builder
     */
    public void warning (SAXParseException e) throws SAXException {
    
        // trap a "!DOCTYPE" error and let the user know
        if (e.getMessage().indexOf("<!DOCTYPE", 0) > 0) {
            // BIG No
            errors.add("Does not have a proper <!DOCTYPE>.  Add or fix it.  It should look something like <!DOCTYPE sim SYSTEM \"file:sim.dtd\">");
            throw e;
            
        } else {

            String entry = "ParseWarning @ line " + e.getLineNumber() + " : " + e.getMessage();
            errors.add(entry);
        }
        // throw e;  // Keep chugging on a warning.
    }

} 
