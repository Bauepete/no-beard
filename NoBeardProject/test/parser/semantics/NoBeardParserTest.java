/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.semantics;

import compiler.NbCompiler;
import error.ErrorHandler;
import nbm.Code;
import nbm.Nbm.Opcode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import parser.NoBeardParser;
import scanner.Scanner;
import scanner.SrcReader;
import scanner.SrcStringReader;
import symlist.SymListManager;

/**
 *
 * @author peter
 */
public class NoBeardParserTest {

    private Code code;
    private SymListManager sym;
    private Scanner scanner;
    private NoBeardParser p;

    public NoBeardParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
 
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class NoBeardParser.
     */
    @Test
    public void testBlockIdentMismatch() {
        System.out.println("testBlockIdentMismatch");

        setupTest(new SrcStringReader("unit foo; do put (x); done fox;"));
        

        assertFalse("False expected", p.parse());
        assertEquals("Sem err ", 1, ErrorHandler.getInstance().getCount("SemErr"));
    }

    @Test
    public void testParseEmpty() {
        System.out.println("parse");

        byte[] expected = {
            Opcode.INC.byteCode(), 0, 0,
            Opcode.HALT.byteCode()
        };

        setupTest(new SrcStringReader("unit foo; do done foo;"));
        

        assertTrue("True expected", p.parse());
        assertCodeEquals("Code ", expected, code.getByteCode());
    }

    @Test
    public void testParse() {
        System.out.println("parse");

        byte[] expected1 = {
            Opcode.INC.byteCode(), 0, 4,
            Opcode.LA.byteCode(), 0, 0, 32,
            Opcode.LIT.byteCode(), 0, 3,
            Opcode.STO.byteCode(),
            Opcode.HALT.byteCode()
        };
        setupTest(new SrcStringReader("unit foo; do int x = 3; done foo;"));
 
        assertTrue("True expected", p.parse());
        assertCodeEquals("Code ", expected1, code.getByteCode());

        byte[] expected2 = {
            Opcode.INC.byteCode(), 0, 4,
            Opcode.LA.byteCode(), 0, 0, 32,
            Opcode.LIT.byteCode(), 0, 3,
            Opcode.STO.byteCode(),
            Opcode.LV.byteCode(), 0, 0, 32,
            Opcode.LIT.byteCode(), 0, 0,
            Opcode.PUT.byteCode(), 0,
            Opcode.HALT.byteCode()
        };
        
        setupTest(new SrcStringReader("unit bah; do int x = 3; put (x); done bah;"));

        assertTrue("True expected", p.parse());
        assertCodeEquals("Code ", expected2, code.getByteCode());

        byte[] expected3 = {
            Opcode.INC.byteCode(), 0, 8,
            Opcode.LA.byteCode(), 0, 0, 32,
            Opcode.LIT.byteCode(), 0, 3,
            Opcode.STO.byteCode(),
            Opcode.LA.byteCode(), 0, 0, 36,
            Opcode.LIT.byteCode(), 0, 1,
            Opcode.STO.byteCode(),
            Opcode.LV.byteCode(), 0, 0, 32,
            Opcode.LV.byteCode(), 0, 0, 36,
            Opcode.ADD.byteCode(),
            Opcode.LIT.byteCode(), 0, 0,
            Opcode.PUT.byteCode(), 0,
            Opcode.HALT.byteCode()
        };
        
        setupTest(new SrcStringReader("unit rsch; do int x = 3; int y = 1; put (x + y); done rsch;"));
        
        
        assertTrue("True expected", p.parse());
        assertCodeEquals("Code ", expected3, code.getByteCode());
    }

    private void assertCodeEquals(String msg, byte[] exp, byte[] act) {
        AssmCodeChecker.assertCodeEquals(msg, exp, act);
    }
    
    private void setupTest(SrcReader src) {
        NbCompiler comp = new NbCompiler(src);
        scanner = comp.getScanner();
        sym = comp.getSymListManager();
        code = comp.getCode();
        p = comp.getParser();
    }
}
