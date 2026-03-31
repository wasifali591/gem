package in.grse.gem.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceSummaryRequestDto {

    @JsonProperty("user")
    private String user;

    @JsonProperty("method")
    private String method;

    @JsonProperty("as_on")
    private String asOn;
}