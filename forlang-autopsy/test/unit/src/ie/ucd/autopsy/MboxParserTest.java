/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.ucd.autopsy;

import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kev D'Arcy
 */
public class MboxParserTest {
    
    public MboxParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isValidMimeTypeMbox method, of class MboxParser.
     */
    @org.junit.Test
    public void testIsValidMimeTypeMbox() {
        System.out.println("isValidMimeTypeMbox");
        byte[] buffer = null;
        boolean expResult = false;
        boolean result = MboxParser.isValidMimeTypeMbox(buffer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class MboxParser.
     */
    @org.junit.Test
    public void testParse() {
        System.out.println("parse");
        File mboxFile = null;
        MboxParser instance = null;
        List<EmailMessage> expResult = null;
        List<EmailMessage> result = instance.parse(mboxFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getErrors method, of class MboxParser.
     */
    @org.junit.Test
    public void testGetErrors() {
        System.out.println("getErrors");
        MboxParser instance = null;
        String expResult = "";
        String result = instance.getErrors();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
