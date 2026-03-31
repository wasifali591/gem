package in.grse.gem.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestDto {

    @JsonProperty("user")
    private String user;

    @JsonProperty("method")
    private String method;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;
}
