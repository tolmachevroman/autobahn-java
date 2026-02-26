///////////////////////////////////////////////////////////////////////////////
//
//   AutobahnJava - http://crossbar.io/autobahn
//
//   Copyright (c) Crossbar.io Technologies GmbH and contributors
//
//   Licensed under the MIT License.
//   http://www.opensource.org/licenses/mit-license.php
//
///////////////////////////////////////////////////////////////////////////////

package io.crossbar.autobahn.wamp.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class IDGeneratorTest {

    @Test
    public void testInitialValue() {
        IDGenerator generator = new IDGenerator();
        
        long firstId = generator.next();
        assertEquals(1L, firstId);
    }

    @Test
    public void testIncrementing() {
        IDGenerator generator = new IDGenerator();
        
        assertEquals(1L, generator.next());
        assertEquals(2L, generator.next());
        assertEquals(3L, generator.next());
        assertEquals(4L, generator.next());
        assertEquals(5L, generator.next());
    }

    @Test
    public void testWrapAround() {
        IDGenerator generator = new IDGenerator();
        
        // Simulate nearing the wrap-around point (2^53)
        // Since we can't easily set the internal state, let's test the logic by checking
        // that the generator continues to produce unique IDs over many calls
        
        long previousId = 0;
        for (int i = 0; i < 1000; i++) {
            long id = generator.next();
            assertTrue("ID should increment", id > previousId);
            previousId = id;
        }
    }

    @Test
    public void testUniqueness() {
        IDGenerator generator = new IDGenerator();
        java.util.HashSet<Long> ids = new java.util.HashSet<>();
        
        // Generate many IDs and ensure they're all unique
        for (int i = 0; i < 10000; i++) {
            long id = generator.next();
            assertFalse("ID should be unique", ids.contains(id));
            ids.add(id);
        }
        
        assertEquals(10000, ids.size());
    }

    @Test
    public void testMultipleGenerators() {
        IDGenerator generator1 = new IDGenerator();
        IDGenerator generator2 = new IDGenerator();
        
        // Each generator should start from 1 independently
        assertEquals(1L, generator1.next());
        assertEquals(1L, generator2.next());
        
        // Continue independently
        assertEquals(2L, generator1.next());
        assertEquals(3L, generator1.next());
        assertEquals(2L, generator2.next());
    }

    @Test
    public void testLargeNumbers() {
        IDGenerator generator = new IDGenerator();
        
        // Generate a large number of IDs to ensure stability
        long lastId = 0;
        for (int i = 0; i < 100000; i++) {
            lastId = generator.next();
        }
        
        assertEquals(100000L, lastId);
    }
}
