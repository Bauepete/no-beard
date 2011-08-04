/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import error.ErrorHandler;
import error.semerr.CantPutOperand;
import error.semerr.TypeExpected;
import error.synerr.SymbolExpected;
import nbm.Code;
import nbm.Nbm.Opcode;
import scanner.Scanner;
import scanner.Scanner.Symbol;
import symlist.Operand;
import symlist.Operand.OperandType;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class PutStatParser extends Parser {

    PutStatParser(Scanner s, SymListManager sym, Code c) {
        super(s, sym, c);
    }

    @Override
    public boolean parse() {
        if (!tokenIsA(Symbol.PUTSY)) {
            ErrorHandler.getInstance().raise(new SymbolExpected(Symbol.PUTSY.toString()));
            return false;
        }

        if (!tokenIsA(Symbol.LPARSY)) {
            return false;
        }

        ExprParser exprP = new ExprParser(scanner, sym, code);
        if (!exprP.parse()) {
            return false;
        }

        // cc
        Operand op = exprP.getOperand();
        if (!isOperandToPut(op)) {
            ErrorHandler.getInstance().raise(new CantPutOperand());
            return false;
        }
        // endcc

        // sem
//        if (op.getType() == OperandType.ARRCHR) {
//            op.emitLoadAddr(code);
//            code.emitOp(Opcode.LIT);
//            code.emitHalfWord(op.getSize());
//        }
//        else {
//            op.emitLoadVal(code);
//        }
        op.emitLoadVal(code);
        // endsem

        switch (scanner.getCurrentToken().getSy()) {
            case COLONSY:
                scanner.nextToken();

                if (!exprP.parse()) {
                    return false;
                }
                // sem
                Operand wOp = exprP.getOperand();
                // endsem
                // cc
                if (wOp.getType() != OperandType.SIMPLEINT) {
                    ErrorHandler.getInstance().raise(new TypeExpected(OperandType.SIMPLEINT.toString()));
                    return false;
                }
                // endcc
                // sem
                wOp.emitLoadVal(code);
                // endsem
                if (!tokenIsA(Symbol.RPARSY)) {
                    ErrorHandler.getInstance().raise(new SymbolExpected(Symbol.RPARSY.toString()));
                    // TODO: Add raiseError() and getNameManager as private methods to Parser
                    // raiseError(new SymbolExpected(getNameManager().getString(Symbol.RPARSY)));
                    return false;
                }
                emitPut(op.getType());
                
                break;

            case RPARSY:
                // sem
                switch (op.getType()) {
                    case SIMPLEINT:
                        code.emitOp(Opcode.LIT);
                        code.emitHalfWord(0);
                        break;
                        
                    case SIMPLECHAR:
                        code.emitOp(Opcode.LIT);
                        code.emitHalfWord(op.getSize());
                        break;
                }
                emitPut(op.getType());
                // endsem
                scanner.nextToken();
                break;

            default:
                ErrorHandler.getInstance().raise(new SymbolExpected(Symbol.COLONSY.toString()));
                return false;
        }
        return true;
    }
    
    private boolean isOperandToPut(Operand op) {
        OperandType opType = op.getType();
        
        return (opType == OperandType.SIMPLECHAR || opType == OperandType.SIMPLEINT);
    }
    
    private void emitPut(OperandType type) {
        switch (type) {
            case SIMPLEINT:
                code.emitOp(Opcode.PUT);
                code.emitByte((byte)0);
                break;
                
            case SIMPLECHAR:
                code.emitOp(Opcode.PUT);
                code.emitByte((byte)1);
                break;
        }
    }
}
