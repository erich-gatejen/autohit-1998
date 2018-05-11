/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit;

import java.io.Serializable;
import java.util.Vector;

import autohit.vm.VMInstruction;

/**
 * Scenario is the basic class for a scenario.  Each represents a test as executed on
 * a single test station.
 * <p>
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 12Feb99</code> 
 * 
 */
public class Scenario implements Serializable {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  A vector containing the Scenario executable.
     *  Each member-object will be a vmInstruction derived class object.
     *
     *  @see autohit.vm.VMInstruction
     *  @serial
     */      	
    public Vector      exec;
    
    /**
     * This scenario's name.
     *
     *  @serial
     */      	
    public String      name;
    
    /**
     * Associated note.
     *  
     *  @serial
     */      	
    public String      note;       

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default Constructor.  It will create an empty Sim.  Remember!  If you are
     *  creating a new Sim, but sure to call init().
     *
     *  @see #init()
     */
    public Scenario() {
        
    }    

    /**
     *  Initializes a brand-new Scenario().
     */
    public void init() {

        exec = new Vector();
    }
    
    /**
     *  Dump this SCENARIO.  I'm putting this in for debugging.  It might have some other
     *  uses...
     *
     *  @return a String containing the dump.
     */
    public String toString() {
    
        StringBuffer d = new StringBuffer();
        
        d.append("Scenario Dump ===============================\n");
        d.append("Name = [" + name + "]\n");
        d.append("Note ----------------------------------- \n");
        d.append(note);
        d.append("\n---------------------------------------- \n");
        d.append("Executable ----------------------------- \n");
        VMInstruction  vmi;
        for (int idx = 0; idx < exec.size(); idx++) {
            d.append("IP = " + idx);
            vmi = (VMInstruction)exec.get(idx);
            d.append(vmi.toString());
        }
        d.append("---------------------------------------- \n");        
        return d.toString();        
    }       
    
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
