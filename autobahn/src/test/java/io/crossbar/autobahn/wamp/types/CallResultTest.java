package io.crossbar.autobahn.wamp.types;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CallResultTest {

    @Test
    public void testCallResultCreation() {
        List<Object> results = Arrays.asList(42, "result", true);
        Map<String, Object> kwresults = new HashMap<>();
        kwresults.put("key1", "value1");

        CallResult callResult = new CallResult(results, kwresults);

        assertNotNull(callResult);
        assertThat(callResult.results).containsExactly(42, "result", true);
        assertThat(callResult.kwresults).containsEntry("key1", "value1");
    }

    @Test
    public void testCallResultWithNullResults() {
        CallResult callResult = new CallResult(null, null);

        assertNotNull(callResult);
        assertNull(callResult.results);
        assertNull(callResult.kwresults);
    }

    @Test
    public void testCallResultWithEmptyResults() {
        List<Object> emptyList = new ArrayList<>();
        Map<String, Object> emptyMap = new HashMap<>();

        CallResult callResult = new CallResult(emptyList, emptyMap);

        assertNotNull(callResult);
        assertThat(callResult.results).isEmpty();
        assertThat(callResult.kwresults).isEmpty();
    }

    @Test
    public void testCallResultWithOnlyPositionalArgs() {
        List<Object> results = Arrays.asList("arg1", 123, 45.6);
        
        CallResult callResult = new CallResult(results, null);

        assertThat(callResult.results).hasSize(3);
        assertNull(callResult.kwresults);
    }

    @Test
    public void testCallResultWithOnlyKeywordArgs() {
        Map<String, Object> kwresults = new HashMap<>();
        kwresults.put("status", "success");
        kwresults.put("code", 200);

        CallResult callResult = new CallResult(null, kwresults);

        assertNull(callResult.results);
        assertThat(callResult.kwresults)
                .hasSize(2)
                .containsEntry("status", "success")
                .containsEntry("code", 200);
    }

    @Test
    public void testCallResultFieldsArePublic() {
        List<Object> results = Arrays.asList("test");
        Map<String, Object> kwresults = new HashMap<>();

        CallResult callResult = new CallResult(results, kwresults);

        // Verify fields are accessible without getters
        assertNotNull(callResult.results);
        assertNotNull(callResult.kwresults);
        assertEquals(results, callResult.results);
        assertEquals(kwresults, callResult.kwresults);
    }

    @Test
    public void testCallResultWithComplexTypes() {
        List<Object> nestedList = Arrays.asList(1, 2, 3);
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("inner", "value");

        List<Object> results = Arrays.asList(nestedList, nestedMap, "simple");
        CallResult callResult = new CallResult(results, null);

        assertThat(callResult.results).hasSize(3);
        assertThat(callResult.results.get(0)).isInstanceOf(List.class);
        assertThat(callResult.results.get(1)).isInstanceOf(Map.class);
    }
}
