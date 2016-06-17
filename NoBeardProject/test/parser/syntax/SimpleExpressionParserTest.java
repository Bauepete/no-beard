/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.syntax;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import parser.SimpleExpressionParser;
import parser.general.SimpleExpressionParserTestSetup;

/**
 *
 * @author peter
 */
@Ignore
public class SimpleExpressionParserTest {

    public SimpleExpressionParserTest() {
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
     * Test of parseOldStyle method, of class SimpleExpressionParser.
     */
    @Test
    public void testAdd() {
        System.out.println("testAdd");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getAddTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testSub() {
        System.out.println("testSub");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getSubTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testNegAdd() {
        System.out.println("testNegAdd");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getNegAddTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testNeg() {
        System.out.println("testNeg");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getNegTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testAddMul() {
        System.out.println("testAddMul");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getAddMulTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testComplexExpr() {
        System.out.println("testComplexExpr");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getComplexExprTestSetup();
        assertEquals("Parse ", true, p.parseOldStyle());
    }

    @Test
    public void testNoExpr() {
        System.out.println("testNoExpr");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getNoExprTestSetup();
        assertEquals("Parse ", false, p.parseOldStyle());
    }
    
    @Test
    public void testOrExpr() {
        System.out.println("testOrExpr");
        SimpleExpressionParser p = SimpleExpressionParserTestSetup.getOrExprTestSetup();
        assertTrue(p.parseOldStyle());
    }
}