/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package creator.compiler;

import java.util.Vector;
import java.util.Stack;
import com.sun.xml.tree.XmlDocument;
import com.sun.xml.tree.XmlDocumentBuilder;
import com.sun.xml.tree.ElementNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList; 
import org.w3c.dom.Node; 
import org.w3c.dom.CharacterData; 

import autohit.Sim;
import autohit.vm.*;

/**
 * This is the a Sim compiler.  It will compile xml documents that conform
 * to the "%AUTOHIT_ROOT%/lib/sim.dtd" dtd into a Sim object.
 * <p>
 * WARNING!!!  For the compiler to work, the FIRST element of the java
 * CLASSPATH <b>must</b> be the root for the autohit installation.  The compiler
 * needs to find the "sim.dtd" file in the "%AUTOHIT_ROOT%/lib" directory.
 * <p>
 * The private methos translate Token() matches the textual tokens, as defined in the
 * dtd to numerics that are used by the rest of the compiler.  Any new tokens
 * need to be added to that method.
 * <p>
 * I can think of several more elegant approaches to making this compiler, but until the
 * XML parsers settle a bit, I'm not going to bother...  
 * <p>
 * IMPORTANT NOTE!!!!  The XML parser WITH DTD ensure instructions are
 * coded in the proper order.  ALL of the code below assumes this!!!
 * If instructions come out of order or in disallowed places 
 * (like a seek outside of a get), I GUARANTEE you'll get a runaway 
 * compiler...,
 * <p>
 * We will look for imbedded variables only in certain locations.
 *
 * @see autohit.Sim
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 14Jan99
 * EPG - Dumb-bug fix - 8Mar99</code> 
 * 
 */
public class SimCompiler extends XmlCompiler {
	
	// --- FINAL FIELDS ------------------------------------------------------	
	private final static String dtdName = "sim.dtd";
	
	private final static int    tokNULL         = 0;
	private final static int    tokNAME         = 1;
	private final static int    tokNOTE         = 2;
	private final static int    tokVERSION      = 3;
	private final static int    tokSET          = 4;
	private final static int    tokFOR          = 5;		
	private final static int    tokWHILE        = 6;
	private final static int    tokIF           = 7;		
	private final static int    tokWAIT         = 8;	
	private final static int    tokGET          = 9;
	private final static int    tokHEADER       = 10;
	private final static int    tokNV           = 11;		
	private final static int    tokBLOCK        = 12;	
	private final static int    tokADD          = 13;
	private final static int    tokVERIFY       = 14;	
	private final static int    tokSEEK         = 15;	
	private final static int    tokEXEC         = 16;
		
	// --- FIELDS ------------------------------------------------------------

    /**
     *  The work-in-progress Sim object.
     */
    protected Sim     workingSim;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.  This is the only and default constructor.
     *
     *  @throws Exception any exception invalidates the compiler.
     */
    public SimCompiler() throws Exception {

        super(dtdName);
    }    

    /**
     *  Compile the xml tree into a Sim object.  This method implements
     *  the abstract method in XmlCompiler.  It will be automatically
     *  called after the source document is parsed into an xml tree.
     *
     *  @param xd   A parsed XML document.
     *  @return a reference to the target object.
     *
     *  @see autohit.Sim
     *  @see creator.compiler.SimCompiler
     */
    public Object build(XmlDocument  xd) {
        
        // Any exception or verification check aborts the compile
        try {

            // Ok, build our working Sim object
            workingSim = new Sim();
            workingSim.init();

            // Get the root element and normalize
            ElementNode root = (ElementNode) xd.getDocumentElement();
            root.normalize();
            
            // Peal out the <info> and <code> sub-trees
            NodeList rootChildren = root.getChildNodes();            
            ElementNode itemTree = (ElementNode) rootChildren.item(0);
            ElementNode codeTree = (ElementNode) rootChildren.item(1);
            
            // Deal with the <info> tree
            for (int idx = 0; idx < itemTree.getLength(); idx++) {
                processItem((ElementNode)itemTree.item(idx));                
            }

            // Deal with the <code> tree
            // Basicall, I'm gonna go wtih recursion.  I don't think it should
            // get very deep.   
            try {
                processCode(codeTree);
                
                // Put a NOP on the end of the executable
                workingSim.exec.add(new VMINop());
                workingSim.exec.trimToSize();
                             
            } catch (Exception e) {
                // an otherwise uncaught exception.  A runaway compiler...
                err.error("Runaway compilation errors.  Stopping compile...");                    
                return null;
            }   
            
        } catch (Exception e) {
            err.error("Compiler internal error.  Stopping compile...");
            return null; // leave the objectCode as null;
        }

        return workingSim;     
    }    



    // == ===============================================================================
    // == =                             PRIVATE METHODS                                 =
    // == ===============================================================================

    
    /**
     *  Processes info section tags. 
     */    
    private void processItem(ElementNode  en) {
    
        switch( translateToken(en.getNodeName()) ) {

            case tokNAME:
                // Pull the child text element and place it the Sim.name;
                String nameText = getText(en.getFirstChild());
                if (nameText == null) {
                    err.error("Empty <name> tag.");        
                } else {
                    workingSim.name = nameText;
                }
                break;
                // For now, we are ignoring UIDs                 
            
            case tokNOTE:
                // Notes will append.  Pull the child text element and paste it to the
                // Sim.note.
                String noteText = getText(en.getFirstChild());
                if (noteText == null) {
                    err.error("Empty <note> tag.");        
                } else {
                    workingSim.note = new String(workingSim.note + noteText);
                }
                break;                  
            
            case tokVERSION:
                // For now, ignore VERSION
                break;
                
            case tokNULL:
            default:
                err.error("Software Detected Fault: creator.compiler.SimCompiler.processItem().  The textual token [" + en.getNodeName() + "] should have NOT reached this code. Check the XML DTD associated with the SimCompiler.");   
            
        }
    }


    /**
     *  Every raw element will enter here.  Bascially, it dispatches it to it's specific
     *  handler.  It will do so for every child in the passed node. 
     */
    private void processCode(ElementNode  en) throws Exception {

        ElementNode  child;

//DEBUG
//System.out.println("ENTER --- " + en.getNodeName());

        for (int idx = 0; idx < en.getLength(); idx++) {

            child = (ElementNode)en.item(idx); 
//DEBUG
//System.out.println("      --- CHILD " + child.getNodeName());

            switch( translateToken(child.getNodeName()) ) {
                
                case tokSET:    
                    handleSet(child);
                    break;
    
                case tokWAIT:
                    handleWait(child);            
                    break;
                    
                case tokGET:
                    handleGet(child);
                    break;                
    
                case tokHEADER:
                    handleHeader(child);
                    break; 
    
                case tokNV:
                    handleNV(child);
                    break;

                case tokFOR:
                    handleFor(child);		
                    break; 
                    
                case tokWHILE:
                    handleWhile(child);
                    break; 
                    
                case tokIF:
                    handleIf(child);
                    break; 
                    
                case tokBLOCK:
                    handleBlock(child);
                    break; 
                    
                case tokADD:
                    handleAdd(child);
                    break; 
                    
                case tokVERIFY:
                    handleVerify(child);
                    break; 

                case tokSEEK:
                    handleSeek(child);
                    break;                 
                
                case tokEXEC:
                    handleExec(child);               
                    break; 
                                    
                case tokNULL:
                default:
                    err.error("Software Detected Fault: creator.compiler.SimCompiler.processItem().  The textual token [" + en.getNodeName() + "] should have NOT reached this code. Check the XML DTD associated with the SimCompiler."); 
                    throw (new Exception("Software Detected Fault in creator.compiler.SimCompiler.processItem()."));     
 
            } // end switch
    
        } // end for
    
//DEBUG
//System.out.println("EXIT --- " + en.getNodeName());    
        
    }    

    // == ===============================================================================
    // == =                         TOKEN HANDLERS                                      =
    // == ===============================================================================

    /**
     *  Emit a set instruction.  Simple enough.
     */
    private void handleSet(ElementNode en) {
 
        String name  = en.getAttribute("name");
        String value = en.getAttribute("value");
        
        if (!isValid(name)) {
            errBadAttribute(en.toString(), "name", "set");       
        } else if (!isValid(value)) {
            errBadAttribute(en.toString(), "value", "set");          
        } else {
               
        
            VMISet  emitable = new VMISet();
            emitable.name  = name;
            emitable.value = value;
            emitable.orIV(seekIV(name)||seekIV(value));              
            
            workingSim.exec.add(emitable);
        }            
    }
    

    /**
     *  Emit an add instruction.
     */
    private void handleAdd(ElementNode en) {
        
        String name  = en.getAttribute("name");
        String value = en.getAttribute("value");
        if (!isValid(name)) {
            errBadAttribute(en.toString(), "name", "add");       
        } else if (!isValid(value)) {
            errBadAttribute(en.toString(), "value", "add");          
        } else {
        
            VMIAdd  emitable = new VMIAdd();
            emitable.name  = name;
            emitable.value = value;
            emitable.orIV(seekIV(name)||seekIV(value));
            
            workingSim.exec.add(emitable);
        }            
    }

    /**
     *  Emit a wait instruction.  Simple enough.
     */
    private void handleWait(ElementNode en) {
    
        String time  = en.getAttribute("time");
        if (isValid(time)) {
            
            VMIWait  emitable = new VMIWait();
            emitable.time = time;
            emitable.orIV(seekIV(time));
            workingSim.exec.add(emitable);
                        
        } else {
            errBadAttribute(en.toString(), "time", "wait"); 
        }
    }   

    /**
     *  Emit a get instruction.
     */
    private void handleGet(ElementNode en) {
    
        String qs  = en.getAttribute("qs");
        if (isValid(qs)) {
            
            // Prepare the instruction
            VMIGet  emitable = new VMIGet();
            emitable.qs = qs;
            emitable.orIV(seekIV(qs));
            
            // Put out a SCOPE
            workingSim.exec.add(new VMIScope());
            
            // Process inner block and add the get
            try {
                processCode(en);
                workingSim.exec.add(emitable);
                
            } catch (Exception e) {
                // Stop an error unravelling here....    
            }
                                
            // Add the RSCOPE
            workingSim.exec.add(new VMIRScope());
                     
        } else {
            errBadAttribute(en.toString(), "qs", "get");
        }
    }   


    /**
     *  Emit a Header instruction.
     */
    private void handleHeader(ElementNode en) {
        
        String name  = en.getAttribute("name");
        String value = en.getAttribute("value");
        if (!isValid(name)) {
            errBadAttribute(en.toString(), "name", "header");       
        } else if (!isValid(value)) {
            errBadAttribute(en.toString(), "value", "header");          
        } else {
        
            VMIHeader  emitable = new VMIHeader();
            emitable.name  = name;
            emitable.value = value;
            emitable.orIV(seekIV(name)||seekIV(value));
            workingSim.exec.add(emitable);
        }            
    }

    /**
     *  Emit a NV instruction.
     */
    private void handleNV(ElementNode en) {
        
        String name      = en.getAttribute("name");
        String valueText = getText(en.getFirstChild());
        if (!isValid(name)) {
            errBadAttribute(en.toString(), "name", "nv");
        } else if (!isValid(valueText)) {
            valueText = "\n";          
        } else {
            VMINV  emitable = new VMINV();
            emitable.name  = name;
            emitable.value = valueText;
            emitable.orIV(seekIV(name)||seekIV(valueText));
            workingSim.exec.add(emitable);
        }            
    }


    /**
     *  handle a block.  easy enough.  just scope it!.
     */
    private void handleBlock(ElementNode en) {
    
        // Put out a SCOPE
        workingSim.exec.add(new VMIScope());
        
        // Process inner block and add the get
        try {
            processCode(en);
            
        } catch (Exception e) {
            // Stop an error unravelling here....    
        }
                            
        // Add the RSCOPE
        workingSim.exec.add(new VMIRScope());           
    }   

    /**
     *  Emit a Verify instruction.
     */
    private void handleVerify(ElementNode en) {
        
        String size  = en.getAttribute("size");
        String crc   = en.getAttribute("crc");

        VMIVerify emitable = new VMIVerify();
        if (isValid(size)) {

            try {
                emitable.size = Integer.parseInt(size); 
            } catch (Exception e) {
                errBadAttribute(en.toString(), "size", "verify");
                return;
            }                           
        }
        
        // Add a CRC instruction, if the attribute is specified.
        if (isValid(crc)) {
            
            try {

                int val = Integer.parseInt(crc);
                VMICrc  eCRC = new VMICrc();
                eCRC.expected = val;
                workingSim.exec.add(eCRC); 
               
            } catch (Exception e) {
                errBadAttribute(en.toString(), "crc", "verify");
                return;  
            }
                
        }
        workingSim.exec.add(emitable);
        
        // Handle any SEEK or EXEC children
        try {
            processCode(en);
            
        } catch (Exception e) {
            // Stop an error unravelling here....    
        }        
    }

    /**
     *  Handle a seek.
     */
    private void handleSeek(ElementNode en) {
        
        String seekText = en.getAttribute("string");
        if (!isValid(seekText)) {
            errBadAttribute(en.toString(), "string", "seek");    
        } else {
            VMISeek  emitable = new VMISeek();
            emitable.expected  = seekText;
            emitable.orIV(seekIV(seekText));
            workingSim.exec.add(emitable);
        }
    }

    /**
     *  Handle an Exec.
     */
    private void handleExec(ElementNode en) {

        // we don't care if the content is mangled or empty.
        
        String content = getText(en.getFirstChild());
        String invoke = en.getAttribute("invoke");
        if (!isValid(invoke)) {
            errBadAttribute(en.toString(), "invoke", "exec");
        } else {
            VMIExec  emitable = new VMIExec();
            emitable.invocation = invoke;
            emitable.content    = content;
            emitable.orIV(seekIV(content)||seekIV(invoke));
            workingSim.exec.add(emitable);
        }
    }

    /**
     *  Handle an IF.
     *
     *  Bounce out any exceptions, cause a screwed IF will spoil a 
     *  parent scope.
     */
    private void handleIf(ElementNode en) throws Exception {
        
        String expression  = en.getAttribute("e");
        String value = en.getAttribute("value");
        if (!isValid(expression)) {
            errBadAttribute(en.toString(), "e", "if");       
        } else if (!isValid(value)) {
            errBadAttribute(en.toString(), "value", "if");          
        } else {
        
            VMIIf  emitable = new VMIIf();
            emitable.e  = expression;
            emitable.value = value;
            emitable.orIV(seekIV(expression)||seekIV(value));
            workingSim.exec.add(emitable);
            // ^^^^ We'll fixup the target in a bit...
            
            // Put out a SCOPE
            workingSim.exec.add(new VMIScope());
            
            // Process inner block and add the get
            try {
                processCode(en);
                
            } catch (Exception e) {
                // Stop inner scope unravellings here...    
            }
                                
            // Add the RSCOPE
            workingSim.exec.add(new VMIRScope());
            
            // Fixup the target
            emitable.target =  workingSim.exec.size();  // Points to where the NEXT intruction
                                                        // should be put...                      
        }            
    }

    /**
     *  Handle an For
     */
    private void handleFor(ElementNode en) {
        
        int    target;
        VMISet initSet =  null;
        
        String count  = en.getAttribute("count");
        String init = en.getAttribute("init");
        if (!isValid(count)) {
            errBadAttribute(en.toString(), "count", "for");       
        } else {
        
            // Prep the FOR
            VMIFor  emitable = new VMIFor();
            emitable.count  = count;
            emitable.orIV(seekIV(count)); 
            
            // Prep the INIT            
            if (init != null) {
            
                initSet = new VMISet();
                initSet.value = init; 
                initSet.name  = count;
                initSet.orIV(seekIV(init)||seekIV(count));
            }    
                    
            // Put out a SCOPE
            workingSim.exec.add(new VMIScope());             

            // Process inner block and add the get
            try {
                
                if (init != null) {
                     workingSim.exec.add(initSet);
                }                
                
                // Prepare a jump back and Emit the FOR
                VMIJump emitjump = new VMIJump();
                emitjump.target = workingSim.exec.size();
                workingSim.exec.add(emitable);
                            
                // Work the inner scope            
                processCode(en);
                
                // Emit the jump and points the FOR right past it.
                workingSim.exec.add(emitjump);
                emitable.target = workingSim.exec.size();                
                
            } catch (Exception e) {
                // Stop inner scope unravellings here...    
            }
                                
            // Add the RSCOPE
            workingSim.exec.add(new VMIRScope());
        }
    }


    /**
     *  Handle an WHILE.
     *
     *  Bounce out any exceptions, cause a screwed IF will spoil a 
     *  parent scope.
     */
    private void handleWhile(ElementNode en) throws Exception {
        
        String expression  = en.getAttribute("e");
        String value = en.getAttribute("value");
        if (!isValid(expression)) {
            errBadAttribute(en.toString(), "e", "while");       
        } else if (!isValid(value)) {
            errBadAttribute(en.toString(), "value", "while");          
        } else {

            // Prep the while
            VMIWhile  emitable = new VMIWhile();
            emitable.e  = expression;
            emitable.value = value;
            emitable.orIV(seekIV(expression)||seekIV(value));
       
            // Emit a SCOPE
            workingSim.exec.add(new VMIScope());
            
            // Emit a NOP and point the while at it
            emitable.target =  workingSim.exec.size();
            workingSim.exec.add(new VMINop()); 
            
            // Process inner block
            try {
                processCode(en);
                
            } catch (Exception e) {
                // Stop inner scope unravellings here...    
            }
                                
            // Emit the WHILE and RSCOPE
            workingSim.exec.add(emitable);            
            workingSim.exec.add(new VMIRScope());
                     
        }            
    }

    // == ===============================================================================
    // == =                             HELPERS                                         =
    // == ===============================================================================
        
    /**
     *  See if a string contains an imbedded variable.
     * 
     *  It uses VMInstruction.IVToken as the delimiter.
     *
     *  @param s the string to check.
     *  @return true if yes, otherwise false
     */
    private boolean seekIV(String  s) {
        
        int sl = s.length() - 1;
        for (int idx = 0; idx < sl; idx++) {
            if ((s.charAt(idx) == VMInstruction.IVToken)&&
                (s.charAt(idx+1) != VMInstruction.IVToken)) 
                return true;           
        } 
        return false;   
    }
    
    /**
     *  Valid string.
     * 
     *  Checks if the reference is not null and the srring contains characters.
     *
     *  @param s the string to check.
     *  @return true if valid, otherwise false
     */
    private boolean isValid(String  s) {        
        if ((s == null)||(s.length() < 1)) return false;
        else return true;   
    }    
    
  
    /**
     *  Translate a textual token into a numeric token.
     *
     *  @param text token text.
     *  @return token numeric.
     */  
    private int translateToken(String  text) {

        // I'm going to let the parser try and optimize this... 
//DEBUG
//System.out.println("XLATE token:  [" + text + "]");
        try {       
            switch(text.charAt(0)) {
                
                case    'n': switch(text.charAt(1)) {
                                case    'a': return tokNAME;
                                case    'o': return tokNOTE;
                                case    'v': return tokNV;
                                default:     return tokNULL;
                             }
                
                case    'v': switch(text.charAt(3)) {
                                case    'i': return tokVERIFY;
                                case    's': return tokVERSION;
                                default:     return tokNULL;
                             }
                             
                case    'w': switch(text.charAt(1)) {
                                case    'h': return tokWHILE;
                                case    'a': return tokWAIT;
                                default:     return tokNULL;
                             }
                
                case    'i': return  tokIF;            
                case    's': switch(text.charAt(2)) {
                                case    't': return tokSET;
                                case    'e': return tokSEEK;
                                default:     return tokNULL;
                             }
                
                case    'f': return  tokFOR;   
                case    'g': return  tokGET;
                case    'h': return  tokHEADER;                    
                case    'b': return  tokBLOCK;
                case    'a': return  tokADD;    
                case    'e': return  tokEXEC;
                                               
                default: return tokNULL;                            
            }
        } catch (Exception e) { }
        return tokNULL;      
    }
    
    /**
     *  Get the text out of an XML node.
     *
     *  @param cdn XML node.
     *  @return the text.
     */  
    private String getText(Node    cdn) {

        try {
            if ((cdn.getNodeType() == Node.TEXT_NODE)||(cdn.getNodeType() != Node. CDATA_SECTION_NODE)) {
                CharacterData   cdnc = (CharacterData)cdn;
                return cdnc.getData();    
            }
        } catch (Exception e) { } // ignore.  re are returning empty anyway
        return null;
    }
    
    /**
     *  Get the text out of an XML node.
     *
     *  @param where where it was bad.
     *  @param which which attribute was bad.
     *  @param tag  which tag.
     *  @return the text.
     */  
    private void errBadAttribute(String where, String which, String tag) {
        err.error("Invalid '" + which + "' attribute for <" + tag + ">.  @" + where);
    }    
    
	// --- INTERNAL CLASSES ---------------------------------------------------	

	// --- DEBUG METHODS ------------------------------------------------------

} 
