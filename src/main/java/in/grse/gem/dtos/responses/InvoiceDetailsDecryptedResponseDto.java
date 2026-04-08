package in.grse.gem.dtos.responses;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InvoiceDetailsDecryptedResponseDto {
    private String sub;
    private String aud;
    private String iss;
    private List<Map<String, Object>> data;
}
