/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.general;

import parser.SimpleExpressionParser;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class SimpleExpressionParserTestSetup extends ParserTestSetup {

    static public SimpleExpressionParser getAddTestSetup() {
        return setupTestObjects("a + b");
    }
    
    static public SimpleExpressionParser getSubTestSetup() {
        return setupTestObjects("a - b");
    }

    static public SimpleExpressionParser getNegAddTestSetup() {
        return setupTestObjects("-a + b");
    }

    static public SimpleExpressionParser getNegTestSetup() {
        return setupTestObjects("-b");
    }

    public static SimpleExpressionParser getAddMulTestSetup() {
        return setupTestObjects("a - b * 3");
    }

    static public SimpleExpressionParser getComplexExprTestSetup() {
        return setupTestObjects("-5 * (a + b)/17");
    }

    static public SimpleExpressionParser getNoExprTestSetup() {
        return setupTestObjects("*b");
    }

    static public SimpleExpressionParser getOrExprTestSetup() {
        return setupBoolTestObjects("a || b || c");
    }

    static private SimpleExpressionParser setupTestObjects(String srcLine) {
        setupInfraStructureOld(srcLine);
        ParserTestSetup.fillSymList(SymListManager.ElementType.INT);
        return new SimpleExpressionParser(scanner, symListManager, code, errorHandler);
    }

    static private SimpleExpressionParser setupBoolTestObjects(String srcLine) {
        setupInfraStructureOld(srcLine);
        fillSymList(SymListManager.ElementType.BOOL);
        return new SimpleExpressionParser(scanner, symListManager, code, errorHandler);
    }
}