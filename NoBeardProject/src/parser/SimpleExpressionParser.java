/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import error.ErrorHandler;
import nbm.CodeGenerator;
import nbm.Nbm;
import nbm.Nbm.Opcode;
import scanner.Scanner;
import scanner.Scanner.Symbol;
import symlist.Operand.OperandType;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class SimpleExpressionParser extends OperandExportingParser {

    private int positionOfLastBooleanOperatorJump;
    private Nbm.Opcode opCode;

    public SimpleExpressionParser(Scanner s, SymListManager sym, CodeGenerator c, ErrorHandler eh) {
        super();
    }

    public SimpleExpressionParser() {

    }

    @Override
    public void parseSpecificPart() {
        parseLeadingSign();
        OperandExportingParser subExpressionParser = createSubExpressionParser();
        parseSymbol(subExpressionParser);
        prepareExportedOperand(subExpressionParser);

        while (currentTokenIsAValidOperator()) {
            parseOperator();
            if (operatorIsBoolean()) {
                handleBooleanSubExpression(subExpressionParser);
            } else {
                handleIntegerSubExpression(subExpressionParser, getLastParsedToken().getSy().toString());
            }
        }
        fixBooleanOperatorChainIfNecessary();
    }

    private static OperandExportingParser createSubExpressionParser() {
        return ParserFactory.create(TermParser.class);
    }

    private boolean operatorIsBoolean() {
        return getLastParsedToken().getSy() == Scanner.Symbol.OR;
    }

    private void fixBooleanOperatorChainIfNecessary() {
        sem(() -> {
            if (positionOfLastBooleanOperatorJump != 0) {
                fixBooleanOperatorChain();
            }
        });
    }

    private void prepareExportedOperand(OperandExportingParser termParser) {
        sem(() -> op2 = termParser.getOperand());
        
        where(opCode == null || op2.getType() == OperandType.SIMPLEINT,
                () -> getErrorHandler().throwOperatorOperandTypeMismatch("+ or -", "int"));
        sem(() -> {
            if (opCode == Nbm.Opcode.SUB) {
                emitCodeForLoadingValue();
                code.emitOp(Opcode.NEG);
            } else {
                exportedOperand = op2;
            }
        });
    }

    private void parseLeadingSign() {
        if (currentTokenIsAValidOperator()) {
            parseOperator();
        }
    }

    private boolean currentTokenIsAValidOperator() {
        Symbol sy = scanner.getCurrentToken().getSy();
        return (sy == Symbol.PLUS || sy == Symbol.MINUS || sy == Symbol.OR);
    }

    private void parseOperator() {
        Scanner.Symbol currentAddOp = scanner.getCurrentToken().getSy();
        parseSymbol(currentAddOp);
        opCode = OperatorToOpCodeMap.getOpCode(currentAddOp);
    }

    private void handleBooleanSubExpression(OperandExportingParser termParser) {
        checkOperandForBeing(op2, OperandType.SIMPLEBOOL, "or");
        maintainBooleanOperatorChain();
        parseSymbol(termParser);
        sem(() -> op2 = termParser.getOperand());
        checkOperandForBeing(op2, OperandType.SIMPLEBOOL, "+ or -");
        emitCodeForLoadingValue();
    }

    private void maintainBooleanOperatorChain() {
        sem(() -> {
            exportedOperand.emitLoadVal(code);
            code.emitOp(Opcode.TJMP);
            code.emitHalfWord(positionOfLastBooleanOperatorJump);
            positionOfLastBooleanOperatorJump = code.getPc() - 2;
        });
    }

    private void fixBooleanOperatorChain() {
        code.emitOp(Opcode.JMP);
        code.emitHalfWord(code.getPc() + 5);
        while (positionOfLastBooleanOperatorJump != 0) {
            int next = code.getCodeHalfWord(positionOfLastBooleanOperatorJump);
            code.fixup(positionOfLastBooleanOperatorJump, code.getPc());
            positionOfLastBooleanOperatorJump = next;
        }
        code.emitOp(Opcode.LIT);
        code.emitHalfWord(1);
    }

    private void handleIntegerSubExpression(OperandExportingParser termParser, String usedOperator) {
        checkOperandForBeing(exportedOperand, OperandType.SIMPLEINT, usedOperator);
        sem(() -> exportedOperand.emitLoadVal(code));
        parseSymbol(termParser);
        fetchOperand(termParser);
        //checkOperandForBeing(exportedOperand, OperandType.SIMPLEINT, usedOperator);
        emitCodeForLoadingValue();
        sem(() -> code.emitOp(opCode));
    }

    @Override
    public boolean parseOldStyle() {
        return true;
    }
}
