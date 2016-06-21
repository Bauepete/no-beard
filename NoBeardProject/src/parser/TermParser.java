/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import error.ErrorHandler;
import nbm.CodeGenerator;
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
public class TermParser extends OperandExportingParser {

    private nbm.Nbm.Opcode opCode;

    private int positionOfLastBooleanOperatorJump;

    public TermParser(Scanner s, SymListManager sym, CodeGenerator c, ErrorHandler e) {
        super();
    }

    public TermParser() {
        this.exportedOperand = new Operand(Operand.OperandKind.ILLEGAL, OperandType.VOID, 0, 0, 0);

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
        return ParserFactory.create(FactorParser.class);
    }

    private boolean operatorIsBoolean() {
        return getLastParsedToken().getSy() == Symbol.AND;
    }
    
    private void parseLeadingSign() {
        
    }

    private void fixBooleanOperatorChainIfNecessary() {
        sem(() -> {
            if (positionOfLastBooleanOperatorJump != 0) {
                fixBooleanOperatorChain();
            }
        });
    }

    private void prepareExportedOperand(OperandExportingParser factorParser) {
        sem(() -> exportedOperand = factorParser.getOperand());
    }

    private boolean currentTokenIsAValidOperator() {
        Symbol sy = scanner.getCurrentToken().getSy();
        return (sy == Symbol.TIMES || sy == Symbol.DIV || sy == Symbol.MOD || sy == Symbol.AND);
    }

    private void parseOperator() {
        Scanner.Symbol currentMulOp = scanner.getCurrentToken().getSy();
        parseSymbol(currentMulOp);
        opCode = OperatorToOpCodeMap.getOpCode(currentMulOp);
    }

    private void handleBooleanSubExpression(OperandExportingParser factorParser) {
        checkOperandForBeing(exportedOperand, OperandType.SIMPLEBOOL, "and");
        sem(() -> exportedOperand.emitLoadVal(code));
        maintainBooleanOperatorChain();
        parseSymbol(factorParser);
        fetchOperand(factorParser);
        checkOperandForBeing(op2, OperandType.SIMPLEBOOL, "and");
        emitCodeForLoadingValue();
    }

    private void maintainBooleanOperatorChain() {
        sem(() -> {
            code.emitOp(Opcode.FJMP);
            code.emitHalfWord(positionOfLastBooleanOperatorJump);
            positionOfLastBooleanOperatorJump = code.getPc() - 2;
        });
    }

    private void handleIntegerSubExpression(OperandExportingParser factorParser, String usedOperator) {
        checkOperandForBeing(exportedOperand, OperandType.SIMPLEINT, usedOperator);
        sem(() -> exportedOperand.emitLoadVal(code));
        parseSymbol(factorParser);
        fetchOperand(factorParser);
        checkOperandForBeing(op2, OperandType.SIMPLEINT, usedOperator);
        emitCodeForLoadingValue();
        sem(() -> code.emitOp(opCode));
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
        code.emitHalfWord(0);
    }

    @Override
    public boolean parseOldStyle() {
        return true;
    }
}
