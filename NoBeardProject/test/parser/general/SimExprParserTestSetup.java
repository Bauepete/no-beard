/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.general;

import error.ErrorHandler;
import error.Error;
import nbm.Code;
import parser.SimExprParser;
import scanner.Scanner;
import scanner.SrcStringReader;
import symlist.Operand;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class SimExprParserTestSetup {

    static private Code c;
    static private SymListManager sym;
    static private Scanner scanner;

    public static Code getCode() {
        return c;
    }

    static public SimExprParser getAddTestSetup() {
        scanner = new Scanner(new SrcStringReader("a + b"));
        return setupTestObjects();
    }

    static public SimExprParser getSubTestSetup() {
        scanner = new Scanner(new SrcStringReader("a - b"));
        return setupTestObjects();
    }

    static public SimExprParser getNegAddTestSetup() {
        scanner = new Scanner(new SrcStringReader("-a + b"));
        return setupTestObjects();
    }

    static public SimExprParser getNegTestSetup() {
        scanner = new Scanner(new SrcStringReader("-b"));
        return setupTestObjects();
    }

    public static SimExprParser getAddMulTestSetup() {
        scanner = new Scanner(new SrcStringReader("a - b * 3"));
        return setupTestObjects();
    }

    static public SimExprParser getComplexExprTestSetup() {
        scanner = new Scanner(new SrcStringReader("-5 * (a + b)/17"));
        return setupTestObjects();
    }

    static public SimExprParser getNoExprTestSetup() {
        scanner = new Scanner(new SrcStringReader("*b"));
        return setupTestObjects();
    }

    static public SimExprParser getOrExprTestSetup() {
        scanner = new Scanner(new SrcStringReader("a || b || c"));
        return setupBoolTestObjects();
    }

    static private SimExprParser setupTestObjects() {
        ErrorHandler.getInstance().reset();
        Error.setScanner(scanner);
        c = new Code();
        sym = new SymListManager(c, scanner);
        sym.newUnit(25);
        sym.newVar(0, SymListManager.ElementType.INT);
        sym.newVar(1, SymListManager.ElementType.INT);
        Operand.setSymListManager(sym);
        scanner.nextToken();
        return new SimExprParser(scanner, sym, c);
    }

    static private SimExprParser setupBoolTestObjects() {
        ErrorHandler.getInstance().reset();
        Error.setScanner(scanner);
        c = new Code();
        sym = new SymListManager(c, scanner);
        sym.newUnit(25);
        sym.newVar(0, SymListManager.ElementType.BOOL);
        sym.newVar(1, SymListManager.ElementType.BOOL);
        sym.newVar(2, SymListManager.ElementType.BOOL);
        Operand.setSymListManager(sym);
        scanner.nextToken();
        return new SimExprParser(scanner, sym, c);
    }
}