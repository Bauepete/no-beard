/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.semantics;

import nbm.Nbm.Opcode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import parser.ExprParser;
import parser.general.ExprParserTestSetup;

/**
 *
 * @author peter
 */
public class ExprParserTest {
    
    public ExprParserTest() {
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
     * Test of parse method, of class ExprParser.
     */
    @Test
    public void testParseSimpleRel() {
        System.out.println("parseSimpleRel");
        byte[] expected = {
            Opcode.LV.byteCode(), 0, 0, 32,
            Opcode.LV.byteCode(), 0, 0, 36,
            Opcode.REL.byteCode(), 0
        };
        ExprParser p = ExprParserTestSetup.getSimpleRel();
        assertTrue(p.parse());
        AssemblerCodeChecker.assertCodeEquals("Code ", expected, ExprParserTestSetup.getCode().getByteCode());
    }

    /**
     * Test of parse method, of class ExprParser.
     */
    @Test
    public void testParseAndRel() {
        System.out.println("parseAndRel");
        byte[] expected = {
            Opcode.LV.byteCode(), 0, 0, 32, // 0: (a <= b)
            Opcode.LV.byteCode(), 0, 0, 36, // 4:
            Opcode.REL.byteCode(), 1,       // 8:
            Opcode.FJMP.byteCode(), 0, 25,  // 10: &&
            Opcode.LV.byteCode(), 0, 0, 36, // 13: (b == 1)
            Opcode.LIT.byteCode(), 0, 1,    // 17:
            Opcode.REL.byteCode(), 2,       // 20:
            Opcode.JMP.byteCode(), 0, 28,   // 22: finalize and expression
            Opcode.LIT.byteCode(), 0, 0     // 25:
        };
        ExprParser p = ExprParserTestSetup.getAndRel();
        assertTrue(p.parse());
        AssemblerCodeChecker.assertCodeEquals("Code ", expected, ExprParserTestSetup.getCode().getByteCode());
    }

    /**
     * Test of parse method, of class ExprParser.
     */
    @Test
    public void testParseOrRel() {
        System.out.println("parseOrRel");
        byte[] expected = {
            Opcode.LV.byteCode(), 0, 0, 32, // 0: (a != b)
            Opcode.LV.byteCode(), 0, 0, 36,
            Opcode.REL.byteCode(), 3,
            Opcode.TJMP.byteCode(), 0, 25,  // 10: &&
            Opcode.LV.byteCode(), 0, 0, 36, // 13: (b >= 1)
            Opcode.LIT.byteCode(), 0, 1,
            Opcode.REL.byteCode(), 4,
            Opcode.JMP.byteCode(), 0, 28,
            Opcode.LIT.byteCode(), 0, 1
        };
        ExprParser p = ExprParserTestSetup.getOrRel();
        assertTrue(p.parse());
        AssemblerCodeChecker.assertCodeEquals("Code ", expected, ExprParserTestSetup.getCode().getByteCode());
    }

    /**
     * Test of parse method, of class ExprParser.
     */
    @Test
    public void testParseAndOrRel() {
        System.out.println("parseAndOrRel");
        // ((a < b) && (b > 1)) || (a < 0)
        byte[] expected = {
            Opcode.LV.byteCode(), 0, 0, 32, // 0: (a < b)
            Opcode.LV.byteCode(), 0, 0, 36,
            Opcode.REL.byteCode(), 0,
            Opcode.FJMP.byteCode(), 0, 25,  // 10: &&
            Opcode.LV.byteCode(), 0, 0, 36, // 13: (b > 1)
            Opcode.LIT.byteCode(), 0, 1,    // 17:
            Opcode.REL.byteCode(), 5,       // 20: 
            Opcode.JMP.byteCode(), 0, 28,   // 22:
            Opcode.LIT.byteCode(), 0, 0,    // 25:
            Opcode.TJMP.byteCode(), 0, 43,  // 28: ||
            Opcode.LV.byteCode(), 0, 0, 40, // 31: (c < 0)
            Opcode.LIT.byteCode(), 0, 0,    // 35:
            Opcode.REL.byteCode(), 0,       // 38:
            Opcode.JMP.byteCode(), 0, 46,   // 40:
            Opcode.LIT.byteCode(), 0, 1     // 43:
            
        };
        ExprParser p = ExprParserTestSetup.getAndOrRel();
        assertTrue(p.parse());
        AssemblerCodeChecker.assertCodeEquals("Code ", expected, ExprParserTestSetup.getCode().getByteCode());
    }
}
