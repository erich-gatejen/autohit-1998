
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import autohit.Sim;

/**
 * CLI dump utility.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 22Jan99</code> 
 * 
 */
public class dump {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------

    /**
     *  Command line compiler....
     *
     *  @see autohit.Sim
     */
    public static void main (String argv []) {

	    if (argv.length != 1) {
	        dump.printHelp();
	        return;
        }
        
        // make sure is starts with a char.
        if (argv[0].charAt(0) == '.') {
             dump.printHelp();
	         return;
        }
        
        // A COMPILED SIM?
        if (argv[0].endsWith(".osm")) {

            // A SIM!!!  do it...
            Sim             aSim;

            try { 
                
                // Read and deserialize
                
            	FileInputStream istream = new FileInputStream(argv [0]);
            	ObjectInputStream p = new ObjectInputStream(istream);	
            	aSim = (Sim)p.readObject();
            	istream.close();
            	
            	System.out.println("dump: Detected a SIM.");
            	
            	System.out.println(aSim.toString());
            	System.out.println("dump: done\n");
                
            } catch (FileNotFoundException e) {
                System.out.println("dump: ERROR: Could not find source file.  Dump aborted\n.");
                return;                               

            } catch (IOException e) {
                System.out.println("dump: ERROR: General file I/O errors.  Dump aborted.");
                System.out.println("dump:      : " + e.getMessage()); 
                
            } catch (Exception e) {
                System.out.println("dump: ERROR: Fatal Error in Dump."); 
                e.printStackTrace ();
            }
                
        } else {
            // Not supported...
            dump.printHelp();
        }

    }

	// --- PRIVATE METHODS ---------------------------------

    public static void printHelp() {
	        System.out.println("Usage: dump file");
	        System.out.println("     : the .ext will detirmine WHICH way to dump.");	        
	        System.out.println("     : the following are currently supported:");
	        System.out.println("     : .osm  == A compiled Sim\n");	          
    }
	


	// --- INTERNAL CLASSES ---------------------------------------------------	

} 