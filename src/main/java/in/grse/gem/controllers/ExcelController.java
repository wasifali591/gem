package in.grse.gem.controllers;

import in.grse.gem.services.DynamicExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final DynamicExcelService dynamicExcelService;

    /**
     * Excel generate
     * 
     * @param json
     * @return
     * @throws Exception
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateExcel(@RequestBody String json) throws Exception {

        byte[] excel = dynamicExcelService.generateDynamicExcel(json);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
