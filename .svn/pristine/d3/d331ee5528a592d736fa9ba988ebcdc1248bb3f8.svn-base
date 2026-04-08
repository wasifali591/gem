package in.grse.gem.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonFlattenUtil {

    // ❌ Prevent instantiation
    private JsonFlattenUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Public method to flatten JSON into Map
     */
    public static Map<String, Object> flatten(JsonNode node) {
        Map<String, Object> result = new LinkedHashMap<>();
        flattenJson("", node, result);
        return result;
    }

    /**
     * Recursive method to flatten JSON
     */
    private static void flattenJson(String prefix, JsonNode node, Map<String, Object> map) {

        if (node == null || node.isNull()) {
            map.put(prefix, null);
            return;
        }

        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();

            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                String newPrefix = prefix.isEmpty() ? field : prefix + "." + field;
                flattenJson(newPrefix, node.get(field), map);
            }
        }

        else if (node.isArray()) {
            int index = 0;
            for (JsonNode item : node) {
                String newPrefix = prefix + "[" + index + "]";
                flattenJson(newPrefix, item, map);
                index++;
            }
        }

        else {
            map.put(prefix, node.asText());
        }
    }
}
