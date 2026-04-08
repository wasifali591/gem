package in.grse.gem.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public final class JsonExpandUtil {

    private JsonExpandUtil() {}

    // 🔥 Public method
    public static List<Map<String, Object>> expand(JsonNode node) {
        return expandNode("", node);
    }

    // 🔁 Recursive expansion
    private static List<Map<String, Object>> expandNode(String prefix, JsonNode node) {

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(new LinkedHashMap<>());

        if (node.isObject()) {

            Iterator<String> fields = node.fieldNames();

            while (fields.hasNext()) {
                String field = fields.next();
                JsonNode child = node.get(field);

                String newPrefix = prefix.isEmpty() ? field : prefix + "." + field;

                List<Map<String, Object>> childRows = expandNode(newPrefix, child);

                rows = merge(rows, childRows);
            }
        }

        else if (node.isArray()) {

            List<Map<String, Object>> arrayRows = new ArrayList<>();

            for (JsonNode item : node) {
                List<Map<String, Object>> childRows = expandNode(prefix, item);
                arrayRows.addAll(childRows);
            }

            return arrayRows;
        }

        else {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put(prefix, node.asText());
            return Collections.singletonList(row);
        }

        return rows;
    }

    // 🔥 Merge rows (Cartesian product)
    private static List<Map<String, Object>> merge(List<Map<String, Object>> base,
                                                   List<Map<String, Object>> incoming) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> b : base) {
            for (Map<String, Object> i : incoming) {

                Map<String, Object> merged = new LinkedHashMap<>(b);
                merged.putAll(i);

                result.add(merged);
            }
        }

        return result;
    }
}
