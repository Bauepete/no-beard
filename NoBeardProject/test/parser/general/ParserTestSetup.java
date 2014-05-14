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
    protected static SymListManager sym;
    protected static Code code;
    protected static ErrorHandler errorHandler;
    
    public static byte[] byteCode() {
        return code.getByteCode();
    }
    
    protected static void setupTestObjects() {
        code = new Code();
        sym = new SymListManager(code, scanner, errorHandler);
        sym.newUnit(25);
        scanner.nextToken();
        sym.newVar(0, SymListManager.ElementType.INT);
        sym.newVar(1, SymListManager.ElementType.INT);
        Operand.setSymListManager(sym);
        Operand.setStringManager(scanner.getStringManager());
    }
    
    protected static void setupScanner(String srcLine) {
        SrcReader sourceReader = new SrcStringReader(srcLine);
        errorHandler = new ErrorHandler(sourceReader);
        scanner = new Scanner(sourceReader, errorHandler);
    }
}
