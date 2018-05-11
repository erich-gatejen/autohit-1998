/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;

import autohit.Sim;
import autohit.utils.Log;
import autohit.transport.Transport;
import autohit.transport.TestTransport;
import autohit.transport.HTTPTransport;
import autohit.SimVM;
import autohit.vm.VMContext;
import autohit.vm.VM;
import autohit.verify.Verify;
import autohit.verify.HTTPVerify;
import autohit.verify.TestVerify;

/**
 * CLI run utility.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 22Jan99</code> 
 * 
 */
public class run {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------

    /**
     *  Command line compiler....
     *
     *  @see autohit.Sim
     */
    public static void main (String argv []) {


        run     thisRun = new run();
        
	    if (argv.length < 2) {
	        thisRun.printHelp();
	        return;
        }
        
        // make sure is starts with a char.
        if (argv[0].charAt(0) == '.') {
             thisRun.printHelp();
	         return;
        }
        
        // A COMPILED SIM?
        if (argv[0].endsWith(".osm")) {

            thisRun.doSim(argv);
                
        } else {
            // Not supported...
            thisRun.printHelp();
        }

    }

	// --- PRIVATE METHODS ---------------------------------

    private void printHelp() {
	        System.out.println("Usage: run file target {http|test}");
	        System.out.println("     : The file .ext will detirmine what VM to use.");
	        System.out.println("     : The target is the transport specific address of the target test machine.");
	        System.out.println("     : You can specify either the http or test transport.  Test is the default.");
	        System.out.println("     : The verification mechanism will be teh same as the transport.");	        
	        System.out.println("     : Any logfile will be named according to system time suffixed with .log.");	 
	        System.out.println("     : The following are currently supported:");
	        System.out.println("     :   .osm  == A compiled Sim.  Selectable transport; use http or test.\n");	          
    }
    
    // == ===============================================================================
    // == =                         HANDLE A SIM (.OSM)                                 =
    // == ===============================================================================

    private void doSim(String  argv []) {

            // A SIM!!!  First, load the SIM
            Sim             aSim;

            try { 
                
                // Read and deserialize
                
            	FileInputStream istream = new FileInputStream(argv [0]);
            	ObjectInputStream p = new ObjectInputStream(istream);	
            	aSim = (Sim)p.readObject();
            	istream.close();
            	
            	System.out.println("run SIM: Detected a SIM.");
            	
            	String trans = null;
            	if (argv.length > 2) {
                    trans = argv[2];
            	} else {
            	    trans = "test";    
            	}
            	
            	runSim(aSim, argv[1], trans);
                
            	System.out.println("run SIM: done\n");
                
            } catch (FileNotFoundException e) {
                System.out.println("run SIM: ERROR: Could not find source file.  Run aborted\n.");
                return;                               

            } catch (IOException e) {
                System.out.println("run SIM: ERROR: General file I/O errors.  Run aborted.");
                System.out.println("run SIM:      : " + e.getMessage()); 
                
            } catch (Exception e) {
                System.out.println("run SIM: ERROR: Fatal Error in Run."); 
                e.printStackTrace ();
            }

    }

    private void runSim(Sim  s, String where, String trans) {
        
        Log         myLog       = null;
        Transport   myTrans     = null;
        Verify      myVerify    = null;

        try {

            myLog    = new Log(1000);
            myLog.pause();
            
            
            if (trans.equalsIgnoreCase("http")) {
                myTrans  = new HTTPTransport();
                myVerify = new HTTPVerify();
                System.out.println("run SIM: Using HTTP transport.");
                
            } else if (trans.equalsIgnoreCase("test")) {
                myTrans  = new TestTransport();
                myVerify = new TestVerify(); 
                System.out.println("run SIM: Using Test transport.");
                                    
            } else {
               System.out.println("run SIM: ERROR: UNKNOWN transport specified.  Aborting.\n");
               this.printHelp();
               return; 
            }
                    
                          
            myTrans.connect(where);
            
            SimVM   svm = new SimVM(s, myLog, myTrans, myVerify);
            
            VMContext sContext = new VMContext();
            sContext.start();
            
            myLog.resume();
            sContext.execute(svm);
            
            // Bleh.  No need to manage control queues with only one context... brute force this dog.
            while (sContext.verifyState() != VM.STATE_NO_VM) {
                Thread.sleep(1000);
            }
                          
            sContext.kill();         
                    
        } catch (Exception e) {
            System.out.println("run SIM: FATAL ERROR: Run aborted.");
            System.out.println("run SIM:      : " + e.getMessage());         
        } finally {
            Date d = new Date();
            myLog.done( "sim-" + (d.getTime()/1000000) + ".log" );         
            myLog.close();                               
        }
    }


	// --- INTERNAL CLASSES ---------------------------------------------------	

} 