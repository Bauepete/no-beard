/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import error.Error;
import error.Error.ErrorType;
import error.ErrorHandler;
import nbm.CodeGenerator;
import scanner.Scanner;
import scanner.Scanner.Symbol;
import symlist.Operand;
import symlist.Operand.OperandType;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public abstract class Parser {

    @FunctionalInterface
    protected interface SemanticAction {
        public void doSemanticAction();
    }
    public void sem(SemanticAction semanticAction) {
        if (parsingWasSuccessfullUntilNow)
            semanticAction.doSemanticAction();
    }
    private boolean parsingWasSuccessfullUntilNow = true;
    
    protected final int NOIDENT = -1;
    protected final int NONUMBER = -1;
    protected Scanner scanner;
    protected SymListManager sym;
    protected CodeGenerator code;
    private final ErrorHandler errorHandler;

    public Parser(Scanner s, SymListManager sym, CodeGenerator c, ErrorHandler errorHandler) {
        this.scanner = s;
        this.sym = sym;
        this.code = c;
        this.errorHandler = errorHandler;
    }

    public abstract boolean parse();

    /**
     * Checks whether the current token is a specific symbol.
     * @param sy The symbol to be checked for.
     * @return true if current token is sy, false otherwise.
     */
    protected boolean tokenIsA(Symbol sy) {
        if (scanner.getCurrentToken().getSy() != sy) {
            getErrorHandler().throwSymbolExpectedError(sy.toString(), scanner.getCurrentToken().getSy().toString());
            return false;
        }
        scanner.nextToken();
        return true;
    }
    
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    protected boolean operandIsA(Operand op, OperandType opType) {
        if (op.getType() != opType) {
            getErrorHandler().raise(new Error(ErrorType.TYPES_EXPECTED, opType.toString()));
            //ErrorHandler.getInstance().raise(new SemErr().new TypeExpected(opType.toString()));
            return false;
        }
        return true;
    }

    /**
     * Checks whether the current token is an identifier.
     * @return The name (spix) of the identifier.
     */
    protected int ident() {
        if (scanner.getCurrentToken().getSy() != Symbol.IDENTIFIER) {
            getErrorHandler().raise(new Error(ErrorType.SYMBOL_EXPECTED, Symbol.IDENTIFIER.toString()));
//            ErrorHandler.getInstance().raise(new SynErr().new IdentifierExpected());
            return NOIDENT;
        }
        int spix = scanner.getCurrentToken().getValue();
        scanner.nextToken();
        return spix;
    }

    protected int number() {
        if (scanner.getCurrentToken().getSy() != Symbol.NUMBER) {
            getErrorHandler().raise(new Error(ErrorType.SYMBOL_EXPECTED, Symbol.NUMBER.toString()));
//            ErrorHandler.getInstance().raise(new SynErr().new SymbolExpected(Symbol.NUMBER.toString()));
            return NONUMBER;
        }
        int val = scanner.getCurrentToken().getValue();
        scanner.nextToken();
        return val;
    }
}
