/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import error.Error;
import nbm.CodeGenerator;
import nbm.NoBeardMachine.Opcode;

/**
 *
 * @author peter
 */
public class ValueOnStackOperand extends Operand {

    public ValueOnStackOperand(Type type, int size, int valaddr, int level) {
        super(Kind.VALUEONSTACK, type, size, valaddr, level);
    }

    public ValueOnStackOperand(Operand op) {
        super(op);
        this.kind = Kind.VALUEONSTACK;
    }
        
    @Override
    public Operand emitLoadVal(CodeGenerator toCode) {
        return new ValueOnStackOperand(this);
    }

    @Override
    public boolean emitAssign(CodeGenerator toCode, Operand destOp) {
        if (!super.emitAssign(toCode, destOp)) {
            return false;
        }
        switch(destOp.getType()) {
            case SIMPLEINT:
            case SIMPLEBOOL:
                toCode.emit(Opcode.STO);
                break;
                
            case SIMPLECHAR:
                toCode.emit(Opcode.STC);
                break;
                
            default:
                String[] tList = {Type.SIMPLEBOOL.toString(), Type.SIMPLECHAR.toString(), Type.SIMPLEINT.toString()};
                errorHandler().throwTypesExpected(tList);
                return false;
        }
        return true;
    }

    @Override
    public Operand emitLoadAddr(CodeGenerator toCode) {
        return new IllegalOperand();
    }
    
}
