package in.grse.gem.dtos.responses;

import lombok.Data;

@Data
public class PaymentResponseDto {

    private String transactionID;
    private String status;
    private String paymentMode;
    private String message;
}