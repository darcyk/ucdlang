/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.ucd.forlang.neo4j.osi;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;

/**
 *
 * @author ensar
 */
public class ShortestPathTest {
    
    public ShortestPathTest() {
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
     * Test of shortestPath method, of class ShortestPath.
     */
    @Test
    public void testShortestPath() {
        System.out.println("shortestPath");
        Node source = null;
        Node target = null;
        String[] types = null;
        Integer depth = null;
        ShortestPath instance = new ShortestPath();
        Iterable<Path> expResult = null;
        Iterable<Path> result = instance.shortestPath(source, target, types, depth);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
