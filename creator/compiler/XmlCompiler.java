/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package creator.compiler;

import java.util.Vector;
import java.io.InputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.EOFException;
import org.xml.sax.InputSource;
import com.sun.xml.parser.Parser;
import com.sun.xml.parser.ValidatingParser;
import com.sun.xml.tree.XmlDocument;
import com.sun.xml.tree.XmlDocumentBuilder;
import org.xml.sax.ErrorHandler; 


/**
 * This is the a base XML compiler.  It must be extended by a specific compiler.  
 * Users of an extended class will call the compile() method in this class, which
 * will first parse the XML then call the abstract method build().  An extended
 * class must override the build() method and use to to compile from the 
 * xml document tree.
 *
 * This will load/cache the DTD by providing a new Resolver that will 
 * return a string reader to the cached DTD.
 *
 * WARNING!!!  For the compiler to work, the FIRST element of the java
 * CLASSPATH <b>must</b> be the root for the autohit installation.  The compiler
 * needs to find the XML DTD files in the "./lib" directory.
 *
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 12Jan99</code> 
 * 
 */
public abstract class XmlCompiler {
	
	// --- FINAL FIELDS ------------------------------------------------------	
	private final static String dtdLocation = "/lib/";
	private final static int    BUFFER_SIZE = 1024;

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Handles parse/compile errors and warnings.  Also serves as the ErrorHandler
     *  for the XML parser.
     *
     *  A new one will be/must be created for each compile.
     *
     *  @see creator.compiler.XmlParseErrorHandler
     */      	
    public XmlParseErrorHandler     err;

    /**
     *  The DTD to use when parsing the source text.
     */      	
    private String                  myDTD;
    
    /**
     *  The DTD to use when parsing the source text.
     */      	
    private XmlCompilerResolver     myResolver;
    
    /**
     *  The XML builder.
     */    
  	private XmlDocumentBuilder	    builder;
	
    /**
     *  The XML parser.
     */    
  	protected Parser		        parser;    
    

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.  You must use this and NOT the default.  
     *  It will make sure that the DTD for the SimLanguage is available.
     *
     *  If you use the defaulty constructor, the compiler will not know
     *  which DTD to use.
     *
     *  @param dtdURI URI of the DTD used in the !DOCTYPE * SYSTEM clause in the
     *                  compile targets.
     *  @throws Exception any exception invalidates the compiler.
     */
    public XmlCompiler(String  dtdURI) throws Exception {

        // Try to find the file
        String raw      = System.getProperty("java.class.path");
        
        String scrubbed      = raw.substring(0, raw.indexOf(";"));
        String scrubbedURI   = dtdURI.substring(dtdURI.indexOf(":") + 1);
        String dtdPath = new String(scrubbed + dtdLocation + scrubbedURI);
        
        // See if we can open and read it.  Allow any exceptions to be
        // thrown beyond this constructor.
        File              dtdFile = new File(dtdPath);
        InputStreamReader inFile  = new InputStreamReader( new FileInputStream(dtdFile) );
        StringBuffer      tempDTD = new StringBuffer();       
        
        try {
            char[]  buf = new char[BUFFER_SIZE];
            int     len;
            
            len = inFile.read(buf, 0, BUFFER_SIZE);
            while (len > 0) {
                tempDTD.append(buf, 0, len);
                len = inFile.read(buf, 0, BUFFER_SIZE);
            }
        
        } catch (EOFException e) {
            // Dont do anything.  this is A-OK.  For some odd reason, java.io
            // will sometimes throw an EOF instead of just returning a -1.
        } catch (Exception e) {
            throw(e);
        }
        
        // Set up a resolver from which the XML parser can get our DTD
        myDTD = tempDTD.toString();   
        
        myResolver = new XmlCompilerResolver(dtdURI, myDTD);
        
        // Prepare the XML document builder and parser
	    builder = new XmlDocumentBuilder();
        parser = new ValidatingParser();              
	    parser.setDocumentHandler(builder);
        parser.setEntityResolver(myResolver);
    }    

    /**
     *  Compile a stream into a new Sim object.  It will abort on a major error
     *  and return a null instead of a Sim instance.  Any compile errors or
     *  warnings can be found in the errors field.
     *
     *  @param is An input stream to the text that is to be compiled.
     *  @return a reference to the target object.
     *
     *  @see autohit.Sim
     */
    public Object compile(InputStream  is) {
        
        XmlDocument     parsedXML;

        Object  objectCode  = null;

        // Any exception aborts the compile
        try {

            // Start fresh
            err = new XmlParseErrorHandler();
            parser.setErrorHandler(err);
        
            // Drive the parser and build the document tree.
    	    parser.parse(new InputSource(is));   
            
            parsedXML = builder.getDocument();
            
// DEBUG
//parsedXML.write (System.out);             
            
            objectCode = build(parsedXML);
           
            
        } catch (Exception e) {
            objectCode = null; // leave the objectCode as null;
        }
       
        return objectCode;       
    }    

    /**
     *  Abstract build method.  Override with a method that builds the object code
     *  from the XML parse tree.
     *
     *  @param xd   A parsed XML document.
     *  
     *  @return Object reference to the object code.
     */
     public abstract Object build(XmlDocument  xd);


	// --- PRIVATE METHODS ---------------------------------------------------	



	// --- INTERNAL CLASSES ---------------------------------------------------	

} 
