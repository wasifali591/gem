package in.grse.gem.dtos.common;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceDataDto {

    private String date;
    private Integer count;
    private List<InvoiceNumberDto> invoiceNumbers;
}
