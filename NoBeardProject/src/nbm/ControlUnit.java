/*
 * Copyright ©2016. Created by P. Bauer (p.bauer@htl-leonding.ac.at),
 * Department of Informatics and Media Technique, HTBLA Leonding, 
 * Limesstr. 12 - 14, 4060 Leonding, AUSTRIA. All Rights Reserved. Permission
 * to use, copy, modify, and distribute this software and its documentation
 * for educational, research, and not-for-profit purposes, without fee and
 * without a signed licensing agreement, is hereby granted, provided that the
 * above copyright notice, this paragraph and the following two paragraphs
 * appear in all copies, modifications, and distributions. Contact the Head of
 * Informatics and Media Technique, HTBLA Leonding, Limesstr. 12 - 14,
 * 4060 Leonding, Austria, for commercial licensing opportunities.
 * 
 * IN NO EVENT SHALL HTBLA LEONDING BE LIABLE TO ANY PARTY FOR DIRECT,
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST
 * PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF HTBLA LEONDING HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * HTBLA LEONDING SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
 * PROVIDED HEREUNDER IS PROVIDED "AS IS". HTBLA LEONDING HAS NO OBLIGATION
 * TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */
package nbm;

import error.ErrorHandler;

/**
 *
 * @author P. Bauer (p.bauer@htl-leonding.ac.at)
 */
public class ControlUnit {

    public enum MachineState {

        STOPPED, RUNNING, ERROR
    }

    private final ProgramMemory programMemory;
    private final DataMemory dataMemory;
    private final CallStack callStack;
    private final ErrorHandler errorHandler;
    private int pc;
    private int currentAddressInProgramMemory;
    private MachineState machineState;

    ControlUnit(ProgramMemory programMemory, DataMemory dataMemory, CallStack callStack, ErrorHandler errorHandler) {
        this.programMemory = programMemory;
        this.dataMemory = dataMemory;
        this.callStack = callStack;
        this.errorHandler = errorHandler;
    }

    public ProgramMemory getProgramMemory() {
        return programMemory;
    }

    public DataMemory getDataMemory() {
        return dataMemory;
    }

    public CallStack getCallStack() {
        return callStack;
    }

    int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    int getLiteral() {
        int n = programMemory.loadHalfWord(currentAddressInProgramMemory);
        currentAddressInProgramMemory += 2;
        return n;
    }

    byte getDisplacement() {
        byte d = programMemory.loadByte(currentAddressInProgramMemory);
        currentAddressInProgramMemory++;
        return d;
    }

    int getBaseFromDisplacement(byte displacement) {
        int base = getCallStack().getFramePointer();
        for (int i = 0; i < displacement; i++) {
            base = getDataMemory().loadWord(base);
        }
        return base;
    }

    int getAddress() {
        int a = programMemory.loadHalfWord(currentAddressInProgramMemory);
        currentAddressInProgramMemory += 2;
        return a;
    }

    void fetchInstruction() {
        currentAddressInProgramMemory = getPc() + 1;
    }

    MachineState getMachineState() {
        return machineState;
    }
    
    void stopDueToDivisionByZero() {
        errorHandler.throwDivisionByZero();
        machineState = MachineState.ERROR;
    }

    public enum Opcode {

        NOP, LIT, LA, LV, LC, STO, STC, ASSN, NEG, ADD, SUB, MUL, DIV, MOD,
        NOT, REL, FJMP, TJMP, JMP, PUT, INC, HALT;

        public byte byteCode() {
            return (byte) this.ordinal();
        }
    }
}
