package in.grse.gem.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.grse.gem.services.DynamicExcelService;
import in.grse.gem.util.JsonExpandUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Slf4j
@Service
public class DynamicExcelServiceImpl implements DynamicExcelService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] generateDynamicExcel(String json) throws Exception {

        // ✅ Step 1: Parse JSON
        JsonNode rootNode = mapper.readTree(json);

        // ✅ Step 2: If wrapper contains "data", extract it
        if (rootNode.has("data") && rootNode.get("data").isArray()) {
            rootNode = rootNode.get("data");
        }

        // ✅ Step 3: Expand JSON → rows
        List<Map<String, Object>> rows = new ArrayList<>();

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                rows.addAll(JsonExpandUtil.expand(node));
            }
        } else {
            rows = JsonExpandUtil.expand(rootNode);
        }

        // ✅ Step 4: Collect headers dynamically
        Set<String> headerSet = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) {
            headerSet.addAll(row.keySet());
        }

        List<String> headers = new ArrayList<>(headerSet);

        // ✅ Step 5: Create Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Header style
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        // Header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }

        // Data rows
        int rowIndex = 1;

        for (Map<String, Object> data : rows) {
            Row row = sheet.createRow(rowIndex++);

            for (int col = 0; col < headers.size(); col++) {
                Object value = data.get(headers.get(col));
                row.createCell(col).setCellValue(value != null ? value.toString() : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert to byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}