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

    private int positionOfLastOrJump;
    private Nbm.Opcode opCode;

    public SimpleExpressionParser(Scanner s, SymListManager sym, CodeGenerator c, ErrorHandler eh) {
        super();
    }

    public SimpleExpressionParser() {

    }

    @Override
    public void parseSpecificPart() {
        sem(() -> positionOfLastOrJump = 0);

        if (currentTokenIsAnAddOp()) {
            parseAddOp();
        }
        TermParser termParser = ParserFactory.create(TermParser.class);
        parseSymbol(termParser);
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

        while (currentTokenIsAnAddOp()) {
            parseAddOp();
            if (getLastParsedToken().getSy() == Scanner.Symbol.OR) {
                handleBooleanTerm(termParser);
            } else {
                handleIntegerTerm(termParser, getLastParsedToken().getSy().toString());
            }
        }
        sem(() -> {
            if (positionOfLastOrJump != 0) {
                fixOrChain();
            }
        });
    }

    private boolean currentTokenIsAnAddOp() {
        Symbol sy = scanner.getCurrentToken().getSy();
        return (sy == Symbol.PLUS || sy == Symbol.MINUS || sy == Symbol.OR);
    }

    private void parseAddOp() {
        Scanner.Symbol currentAddOp = scanner.getCurrentToken().getSy();
        parseSymbol(currentAddOp);
        opCode = OperatorToOpCodeMap.getOpCode(currentAddOp);
    }

    private void handleBooleanTerm(TermParser termParser) {
        checkOperandForBeing(op2, OperandType.SIMPLEBOOL, "or");
        maintainOrChain();
        parseSymbol(termParser);
        sem(() -> op2 = termParser.getOperand());
        checkOperandForBeing(op2, OperandType.SIMPLEBOOL, "+ or -");
        emitCodeForLoadingValue();
    }

    private void maintainOrChain() {
        sem(() -> {
            exportedOperand.emitLoadVal(code);
            code.emitOp(Opcode.TJMP);
            code.emitHalfWord(positionOfLastOrJump);
            positionOfLastOrJump = code.getPc() - 2;
        });
    }

    private void fixOrChain() {
        code.emitOp(Opcode.JMP);
        code.emitHalfWord(code.getPc() + 5);
        while (positionOfLastOrJump != 0) {
            int next = code.getCodeHalfWord(positionOfLastOrJump);
            code.fixup(positionOfLastOrJump, code.getPc());
            positionOfLastOrJump = next;
        }
        code.emitOp(Opcode.LIT);
        code.emitHalfWord(1);
    }

    private void handleIntegerTerm(TermParser termParser, String usedOperator) {
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
