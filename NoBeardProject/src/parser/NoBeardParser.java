/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import error.ErrorHandler;
import nbm.Nbm;
import nbm.CodeGenerator;
import scanner.NameManager;
import scanner.Scanner;
import scanner.Scanner.Symbol;
import symlist.SymListEntry;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class NoBeardParser extends Parser {

    public NoBeardParser(Scanner s, SymListManager sym, CodeGenerator c, ErrorHandler e) {
        super(s, sym, c, e);
    }

    @Override
    public boolean parse() {
        if (!tokenIsA(Symbol.UNITSY)) {
            return false;
        }

        int name = ident();
        if (name == NOIDENT) {
            return false;
        }

        if (!tokenIsA(Symbol.SEMICOLONSY)) {
            return false;
        }

        // sem
        sym.newUnit(name);
        SymListEntry unitObj = sym.findObject(name);
        // endsem

        if (!block(unitObj)) {
            return false;
        }

        // sem
        code.emitOp(Nbm.Opcode.HALT);
        // endsem

        int name1 = ident();
        if (name1 == NOIDENT) {
            return false;
        }
        if (!tokenIsA(Symbol.SEMICOLONSY)) {
            return false;
        }

        // cc
        if (name != name1) {
            NameManager n = scanner.getNameManager();
            String[] pList = {n.getStringName(name), n.getStringName(name1)};
            getErrorHandler().raise(new error.Error(error.Error.ErrorType.BLOCK_NAME_MISSMATCH, pList));
            return false;
        }
        // end cc
        return true;
    }

    private boolean block(SymListEntry obj) {
        BlockParser blockP = new BlockParser(scanner, sym, code, obj, getErrorHandler());
        return blockP.parse();
    }
}
