/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.general;

import error.ErrorHandler;
import nbm.Code;
import scanner.Scanner;
import scanner.SrcReader;
import scanner.SrcStringReader;
import symlist.Operand;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class ParserTestSetup {
    protected static Scanner scanner;
    protected static SymListManager symListManager;
    protected static Code code;
    protected static ErrorHandler errorHandler;
    
    public static byte[] getByteCode() {
        return code.getByteCode();
    }
    
    protected static void setupInfraStructure(String srcLine) {
        SrcReader sourceReader = new SrcStringReader(srcLine);
        errorHandler = new ErrorHandler(sourceReader);
        scanner = new Scanner(sourceReader, errorHandler);
        code = new Code(256);
        symListManager = new SymListManager(code, scanner, errorHandler);
        scanner.nextToken();
        Operand.setSymListManager(symListManager);
        Operand.setStringManager(scanner.getStringManager());
    }
    
    protected static void fillSymList(SymListManager.ElementType type) {
        symListManager.newUnit(25);
        symListManager.newVar(0, type);
        symListManager.newVar(1, type);
        symListManager.newVar(2, type);

    }
}
