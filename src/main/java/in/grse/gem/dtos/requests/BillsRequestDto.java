package in.grse.gem.dtos.requests;

import lombok.Data;

@Data
public class BillsRequestDto {

    private String user;     // DEMO
    private String method;   // getbills
    private String inv_id;   // invoice id
}