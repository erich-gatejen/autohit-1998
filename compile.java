import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import autohit.Sim;
import creator.compiler.SimCompiler;

/**
 * CLI compiler.  It will assume the compiler to use based on
 * the extension.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 22Jan99</code> 
 * 
 */
public class compile {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------

    /**
     *  Command line compiler....
     *
     *  @see autohit.Sim
     */
    public static void main (String argv []) {

	    if (argv.length != 2) {
	        compile.printHelp();
	        return;
        }
        
        // make sure is starts with a char.
        if (argv[0].charAt(0) == '.') {
             compile.printHelp();
	         return;
        }
        
        // a SIM?
        if (argv[0].endsWith(".sim")) {

            Sim             aSim;
            SimCompiler     aCompiler = null;

            // A SIM!!!  do it...
            try { 
                
                // Read and compile
                
                System.out.println("compile: Start Sim compile of " + argv [0]);
                
                FileInputStream  is = new FileInputStream(argv [0]);
                aCompiler = new SimCompiler();                
                aSim      = (Sim) aCompiler.compile(is);
                
                if ( (aSim != null)&&(aCompiler.err.errors.size() == 0) ) {
                
                    // Serialize
                    FileOutputStream   ostream = new FileOutputStream(argv[1] + ".osm");
    	            ObjectOutputStream sobj    = new ObjectOutputStream(ostream);	
    	            sobj.writeObject(aSim);	
    	            sobj.flush();	
    	            ostream.close();
    
                    // Print warnings
                    aCompiler.err.prettyPrint();
    	            	            
    	            System.out.println("compile: SUCCESS!\n");
                    System.out.println("compile: done\n");	            
	                return;
	                
	            }  else {
	                System.out.println("compile: ERROR: Errors in compilation.  Object not made.");
	            }  
                
            } catch (FileNotFoundException e) {
                System.out.println("compile: ERROR: Could not find source file.  Compile aborted\n.");
                return;                               

            } catch (IOException e) {
                System.out.println("compile: ERROR: General file I/O errors.  Compile aborted.");
                System.out.println("compile:      : " + e.getMessage()); 
                
            } catch (Exception e) {
                System.out.println("compile: ERROR: Fatal Error in Compiler.  Object not made."); 
                e.printStackTrace ();
            } 
            
            // Dump errors...
            System.out.println("compile: ERRORS: Errors and Warnings ------>\n");
            if (aCompiler != null) System.out.println(aCompiler.err.prettyPrint());
            System.out.println("compile: done\n");
                
        } else {
            // Not supported...
            compile.printHelp();
        }

    }

	// --- PRIVATE METHODS ---------------------------------

    public static void printHelp() {
	        System.out.println("Usage: compile sourcefile.ext objectfile");
	        System.out.println("     : the .ext will detirmine WHICH compiler is used.  The");	        
	        System.out.println("     : the following are currently supported:");
	        System.out.println("     : .sim  == Sim compiler.  Object file extension is .osm\n");	          
    }
	


	// --- INTERNAL CLASSES ---------------------------------------------------	

} 