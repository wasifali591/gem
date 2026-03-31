package in.grse.gem.services;

import com.fasterxml.jackson.databind.JsonNode;
import in.grse.gem.dtos.common.RequestDto;
import in.grse.gem.dtos.requests.*;
import in.grse.gem.dtos.responses.*;

public interface GemService {

    LoginResponseDto login();
    JsonNode getInvoiceSummary(
            InvoiceSummaryRequestDto requestDto);

    JsonNode getInvoiceDetails(InvoiceDetailsRequestDto requestDto);

    JsonNode makePayment(PaymentRequestDto requestDto);

    JsonNode getOrderDetails(OrdersRequestDto requestDto);

    JsonNode getBillDetails(BillsRequestDto request);

    JsonNode getOrderSummary(RequestDto request);

    JsonNode getCracSummary(RequestDto request);

    JsonNode getCracServiceSummary(RequestDto request);

    JsonNode getGoodCracDetails(RequestDto request);

    JsonNode getBillSummary(RequestDto request);
}