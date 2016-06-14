/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symlist;

import error.Error.ErrorType;
import nbm.CodeGenerator;
import nbm.Nbm.Opcode;
import symlist.Operand.OperandKind;
import symlist.Operand.OperandType;

/**
 *
 * @author peter
 */
public class ConstantOperand extends Operand {
    
    public ConstantOperand(OperandType type, int size, int valaddr, int level) {
        super(OperandKind.CONSTANT, type, size, valaddr, level);
    }
    
    public ConstantOperand(Operand op) {
        super(op);
    }
    
    @Override
    public Operand emitLoadVal(CodeGenerator toCode) {
        if (super.emitLoadVal(toCode) == null) {
            return new IllegalOperand();
        }
        switch(type) {
            case SIMPLEINT:
            case SIMPLEBOOL:
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(valaddr);
                break;
                
            case SIMPLECHAR:
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(getStringStorage(valaddr));
                break;
        }
    return (new ValueOnStackOperand(this));
    }

    @Override
    public boolean emitAssign(CodeGenerator toCode, Operand destOp) {
        if (!super.emitAssign(toCode, destOp)) {
            return false;
        }
        switch (destOp.getType()) {
            case SIMPLEINT:
            case SIMPLEBOOL:
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(valaddr);
                toCode.emitOp(Opcode.STO);
                break;
                
            case SIMPLECHAR:
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(getStringStorage(valaddr));
                toCode.emitOp(Opcode.STC);
                break;
                
            case ARRAYCHAR:
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(valaddr);
                toCode.emitOp(Opcode.LIT);
                toCode.emitHalfWord(destOp.getSize());
                toCode.emitOp(Opcode.ASSN);
                break;
                
            default:
                String[] tList = {OperandType.SIMPLEBOOL.toString(), OperandType.SIMPLECHAR.toString(),
                    OperandType.SIMPLEINT.toString(), OperandType.ARRAYCHAR.toString()};
                errorHandler().raise(new error.Error(ErrorType.TYPES_EXPECTED, tList));
                return false;
        }
        return true;
    }

    @Override
    public Operand emitLoadAddr(CodeGenerator toCode) {
        if (super.emitLoadAddr(toCode) == null) {
            return new IllegalOperand();
        }
        
        Operand returnedOp;
        
        if (getType() == OperandType.SIMPLECHAR || getType() == OperandType.ARRAYCHAR) {
            toCode.emitOp(Opcode.LIT);
            toCode.emitHalfWord(valaddr);
            returnedOp = new AddrOnStackOperand(this);
        } else {
            String[] tList = {OperandType.SIMPLECHAR.toString(), OperandType.ARRAYCHAR.toString()};
            errorHandler().raise(new error.Error(ErrorType.TYPES_EXPECTED, tList));
            returnedOp = new IllegalOperand();
        }
        return returnedOp;
    }
}
