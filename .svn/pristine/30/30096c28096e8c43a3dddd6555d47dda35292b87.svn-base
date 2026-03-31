package in.grse.gem.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import in.grse.gem.dtos.common.RequestDto;
import in.grse.gem.dtos.requests.*;
import in.grse.gem.dtos.responses.*;
import in.grse.gem.services.GemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gem")
@RequiredArgsConstructor
public class GemController {

    private final GemService gemService;

    @PostMapping("/login")
    public LoginResponseDto login() {
        return gemService.login();
    }


    @PostMapping("/invoice-summary")
    public JsonNode getInvoiceSummary(
            @RequestBody InvoiceSummaryRequestDto requestDto) {

        return gemService.getInvoiceSummary(requestDto);
    }

    @PostMapping("/get-invoice")
    public JsonNode getInvoiceDetails(
            @RequestBody InvoiceDetailsRequestDto requestDto) {

        return gemService.getInvoiceDetails(requestDto);
    }

    @PostMapping("/payment")
    public JsonNode makePayment(@RequestBody PaymentRequestDto requestDto) {
        return gemService.makePayment(requestDto);
    }

    @PostMapping("/get-orders")
    public JsonNode getOrders(
            @RequestBody OrdersRequestDto requestDto) {

        return gemService.getOrderDetails(requestDto);
    }

    @PostMapping("/bills")
    public JsonNode getBills(@RequestBody BillsRequestDto request) {

        return gemService.getBillDetails(request);
    }

    @PostMapping("/order-summary")
    public JsonNode getOrderSummary(@RequestBody RequestDto request) {
        return gemService.getOrderSummary(request);
    }

    @PostMapping("/crac-summary")
    public JsonNode getCracSummary(@RequestBody RequestDto request) {
        return gemService.getCracSummary(request);
    }

    @PostMapping("/crac-service-summary")
    public JsonNode getCracServiceSummary(
            @RequestBody RequestDto request) {

        return gemService.getCracServiceSummary(request);
    }

    @PostMapping("/good-crac-details")
    public JsonNode getCrac(@RequestBody RequestDto request) {
        return gemService.getGoodCracDetails(request);
    }

    @PostMapping("/bill-summary")
    public JsonNode getBillSummary(@RequestBody RequestDto request) {
        return gemService.getBillSummary(request);
    }

}