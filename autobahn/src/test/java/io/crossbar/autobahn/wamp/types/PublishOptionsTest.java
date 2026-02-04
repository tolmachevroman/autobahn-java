package io.crossbar.autobahn.wamp.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class PublishOptionsTest {

    @Test
    public void testPublishOptionsWithAcknowledge() {
        PublishOptions options = new PublishOptions(true, false);

        assertTrue("Acknowledge should be true", options.acknowledge);
        assertFalse("ExcludeMe should be false", options.excludeMe);
        assertFalse("Retain should be false (default)", options.retain);
    }

    @Test
    public void testPublishOptionsExcludeMe() {
        PublishOptions options = new PublishOptions(false, true);

        assertFalse("Acknowledge should be false", options.acknowledge);
        assertTrue("ExcludeMe should be true", options.excludeMe);
        assertFalse("Retain should be false (default)", options.retain);
    }

    @Test
    public void testPublishOptionsAllTrue() {
        PublishOptions options = new PublishOptions(true, true);

        assertTrue("Acknowledge should be true", options.acknowledge);
        assertTrue("ExcludeMe should be true", options.excludeMe);
        assertFalse("Retain should be false (default)", options.retain);
    }

    @Test
    public void testPublishOptionsWithRetain() {
        PublishOptions options = new PublishOptions(true, false, true);

        assertTrue("Acknowledge should be true", options.acknowledge);
        assertFalse("ExcludeMe should be false", options.excludeMe);
        assertTrue("Retain should be true", options.retain);
    }

    @Test
    public void testPublishOptionsFieldsArePublic() {
        PublishOptions options = new PublishOptions(false, false, false);

        // Verify all fields are accessible
        assertFalse(options.acknowledge);
        assertFalse(options.excludeMe);
        assertFalse(options.retain);
    }

    @Test
    public void testPublishOptionsAllFalse() {
        PublishOptions options = new PublishOptions(false, false, false);

        assertFalse("All options should be false", 
                options.acknowledge || options.excludeMe || options.retain);
    }
}
