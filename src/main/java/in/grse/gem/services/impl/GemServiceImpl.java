package in.grse.gem.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import in.grse.gem.dtos.common.RequestDto;
import in.grse.gem.dtos.requests.*;
import in.grse.gem.dtos.responses.*;
import in.grse.gem.services.GemService;
import in.grse.gem.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;

@Slf4j
@Service
public class GemServiceImpl implements GemService {

    private static final String GEM_URL = "https://api.gemorion.org/erp/webs/";

    @Value("${gem.secret.key}")
    private String secretKey;
    @Value("${gem.username}")
    private String username;
    @Value("${gem.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public LoginResponseDto login() {

        log.info("Calling GeM Login service");
        long start = System.currentTimeMillis();

        RestTemplate restTemplate = new RestTemplate();

        // Prepare request body
        LoginRequestDto request = new LoginRequestDto();
        request.setUser(username);
        request.setPass(password);
        request.setMethod("login");

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            log.info("Calling external GeM API");

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            GEM_URL,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
            long duration = System.currentTimeMillis() - start;

            log.info("GeM API responded with status {} in {} ms",
                    response.getStatusCode(),
                    duration);
            LoginResponseDto dto =
                    mapper.readValue(response.getBody(), LoginResponseDto.class);

            log.info("GeM login successful");
            return dto;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("GeM login failed after {} ms", duration, e);

            LoginResponseDto error = new LoginResponseDto();
            error.setStatus("error");
            return error;
        }
    }

    @Override
    public JsonNode getInvoiceSummary(InvoiceSummaryRequestDto requestDto) {
        log.info("Starting GeM Invoice Summary Flow");

        try {
            // ✅ Step 1: Login & get token
            LoginResponseDto token = login();

            // ✅ Step 2: Call Invoice API
            String encryptedResponse = callInvoiceApi(requestDto, token.getToken());

            // ✅ Step 3: Extract & decrypt
            JsonNode root = mapper.readTree(encryptedResponse);
            String encryptedData = root.get("data").asText();

            String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

            log.info("Decrypted Response: {}", decryptedJson);

            JsonNode decryptedNode = mapper.readTree(decryptedJson);

            // ✅ Step 4: Convert to DTO
            return decryptedNode;
            //mapper.readValue(decryptedJson, InvoiceSummaryDecryptedResponseDto.class);


        } catch (Exception e) {
            log.error("Error in GeM flow", e);
            throw new RuntimeException("GeM API failed", e);
        }
    }

    /**
     * @param requestDto
     * @return
     */
    @Override
    public JsonNode getInvoiceDetails(InvoiceDetailsRequestDto requestDto) {
        log.info("Calling GeM getInvoice API");

        try {
            // ✅ Step 1: Login → get token
            LoginResponseDto token = login();

            // ✅ Step 2: Prepare request
            requestDto.setUser(requestDto.getUser());
            requestDto.setMethod("getInvoice");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            // Optional (if required like curl)
//            headers.set("Cookie", "ci_session=pummi1endsdmbe93ddmngjg6gi1ik3g0");

            HttpEntity<InvoiceDetailsRequestDto> entity =
                    new HttpEntity<>(requestDto, headers);

            // ✅ Step 3: Call API
            ResponseEntity<String> response =
                    restTemplate.exchange(GEM_URL, HttpMethod.POST, entity, String.class);

            // ✅ Step 4: Extract encrypted data
            JsonNode root = mapper.readTree(response.getBody());
            String encryptedData = root.get("data").asText();

            // ✅ Step 5: Decrypt
            String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

            log.info("Decrypted Response: {}", decryptedJson);
            JsonNode decryptedNode = mapper.readTree(decryptedJson);

            // ✅ Step 6: Convert to DTO
            return decryptedNode;
            //mapper.readValue(decryptedJson, InvoiceDetailsDecryptedResponseDto.class);

        } catch (Exception e) {
            log.error("Error calling getInvoice API", e);
            throw new RuntimeException("GeM getInvoice API failed", e);
        }
    }

    /**
     * @param requestDto
     * @return
     */
    @Override
    public JsonNode makePayment(PaymentRequestDto requestDto) {
        try {
            LoginResponseDto token = login();

            ObjectMapper mapper = new ObjectMapper();

            // Step 1: Convert DTO → Map
            Map map = mapper.convertValue(requestDto, Map.class);

            // Remove user & method
            map.remove("user");
            map.remove("method");

            // Step 2: Create paydata JSON
            String payDataJson = mapper.writeValueAsString(map);

            // Step 3: Encrypt
            String encryptedPayData = AESUtil.encrypt(payDataJson, secretKey);

            // Step 4: Final request
            Map<String, Object> finalRequest = new HashMap<>();
            finalRequest.put("user", requestDto.getUser());
            finalRequest.put("method", requestDto.getMethod());
            finalRequest.put("paydata", encryptedPayData);

            // Step 5: Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(finalRequest, headers);

            // Step 6: Call GeM API (DIRECT DTO mapping)
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            GEM_URL,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
            // ✅ Step 4: Extract encrypted data
            JsonNode root = mapper.readTree(response.getBody());
//            String encryptedData = root.get("data").asText();

            // ✅ Step 5: Decrypt
//            String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

//            log.info("Decrypted Response: {}", decryptedJson);
//            JsonNode decryptedNode = mapper.readTree(encryptedData);

            // ✅ Step 6: Convert to DTO
            return root;

        } catch (Exception e) {
            throw new RuntimeException("Payment API failed", e);
        }
    }

    /**
     * @param requestDto
     * @return
     */
    @Override
    public JsonNode getOrderDetails(OrdersRequestDto requestDto) {
        log.info("Calling GeM getOrders API");

        try {
            // ✅ Step 1: Login → get token
            LoginResponseDto token = login();

            // ✅ Step 2: Prepare request
            requestDto.setUser(requestDto.getUser());
            requestDto.setMethod("getOrders");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            // Optional (if required like curl)
//            headers.set("Cookie", "ci_session=pdqvt9h806gnhki52n1opm1gflcrj4lu");

            HttpEntity<OrdersRequestDto> entity =
                    new HttpEntity<>(requestDto, headers);

            // ✅ Step 3: Call API
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            GEM_URL,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

            // ✅ Step 4: Extract encrypted data
            JsonNode root = mapper.readTree(response.getBody());
            String encryptedData = root.get("data").asText();

            // ✅ Step 5: Decrypt
            String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

            log.info("Decrypted Response: {}", decryptedJson);
            JsonNode decryptedNode = mapper.readTree(decryptedJson);

            // ✅ Step 6: Convert to DTO
            return decryptedNode;
            //mapper.readValue(decryptedJson, OrdersResponseDto.class);

        } catch (Exception e) {
            log.error("Error calling getOrders API", e);
            throw new RuntimeException("GeM getOrders API failed", e);
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getBillDetails(BillsRequestDto request) {
        try {
            // ✅ Step 1: Get Token
            LoginResponseDto token = login();

            // ✅ Step 2: Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<BillsRequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 3: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 4: Extract encrypted data
            String encryptedData = root.get("data").asText();

            // ✅ Step 5: Decrypt
            String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

            log.info("Decrypted JSON: {}", decryptedJson);

            JsonNode decryptedNode = mapper.readTree(decryptedJson);

            // ✅ Step 6: Build FINAL RESPONSE (IMPORTANT)
//            ObjectNode finalResponse = mapper.createObjectNode();
//
//            finalResponse.put("sub", decryptedNode.get("sub").asText());
//            finalResponse.put("aud", decryptedNode.get("aud").asText());
//            finalResponse.put("iss", decryptedNode.get("iss").asText());
//            finalResponse.set("data", decryptedNode.get("data"));

            return decryptedNode;

        } catch (Exception e) {
            log.error("Error in getBills", e);
            throw new RuntimeException("Failed to fetch bills");
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getOrderSummary(RequestDto request) {
        try {
            // ✅ Step 1: Get Token
            LoginResponseDto token = login();

            // ✅ Step 1: Headers (NO TOKEN)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<RequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 2: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 3: Decrypt if needed
            if (root.has("data")) {

                String encryptedData = root.get("data").asText();

                String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

                log.info("Decrypted JSON: {}", decryptedJson);

                JsonNode decryptedNode = mapper.readTree(decryptedJson);

                // ✅ Step 4: Build FINAL RESPONSE (same format as GeM)
//                ObjectNode finalResponse = mapper.createObjectNode();
//
//                finalResponse.put("sub", decryptedNode.get("sub").asText());
//                finalResponse.put("aud", decryptedNode.get("aud").asText());
//                finalResponse.put("iss", decryptedNode.get("iss").asText());
//                finalResponse.set("data", decryptedNode.get("data"));

                return decryptedNode;
            }

            // ✅ Step 5: If already plain JSON
            return root;

        } catch (Exception e) {
            log.error("Error in orderSummary API", e);
            throw new RuntimeException("Failed to fetch order summary");
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getCracSummary(RequestDto request) {
        try {
            // ✅ Step 1: Headers (NO TOKEN)
            // ✅ Step 1: Get Token
            LoginResponseDto token = login();

            // ✅ Step 1: Headers (NO TOKEN)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<RequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 2: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 3: Decrypt response
            if (root.has("data")) {

                String encryptedData = root.get("data").asText();

                String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

                log.info("Decrypted JSON: {}", decryptedJson);

                JsonNode decryptedNode = mapper.readTree(decryptedJson);

                // ✅ Step 4: Build FINAL RESPONSE
//                ObjectNode finalResponse = mapper.createObjectNode();
//
//                finalResponse.put("sub", decryptedNode.get("sub").asText());
//                finalResponse.put("aud", decryptedNode.get("aud").asText());
//                finalResponse.put("iss", decryptedNode.get("iss").asText());
//                finalResponse.set("data", decryptedNode.get("data"));

                return decryptedNode;
            }

            // ✅ Step 5: If already plain
            return root;

        } catch (Exception e) {
            log.error("Error in cracSummary API", e);
            throw new RuntimeException("Failed to fetch CRAC summary");
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getCracServiceSummary(RequestDto request) {
        try {
            // ✅ Step 1: Headers (NO TOKEN)
            LoginResponseDto token = login();

            // ✅ Step 1: Headers (NO TOKEN)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<RequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 2: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 3: Decrypt if encrypted
            if (root.has("data")) {

                String encryptedData = root.get("data").asText();

                String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

                log.info("Decrypted JSON: {}", decryptedJson);

                JsonNode decryptedNode = mapper.readTree(decryptedJson);

                // ✅ Step 4: Build FINAL RESPONSE
//                ObjectNode finalResponse = mapper.createObjectNode();
//
//                finalResponse.put("sub", decryptedNode.get("sub").asText());
//                finalResponse.put("aud", decryptedNode.get("aud").asText());
//                finalResponse.put("iss", decryptedNode.get("iss").asText());
//                finalResponse.set("data", decryptedNode.get("data"));

                return decryptedNode;
            }

            // ✅ Step 5: If already plain
            return root;

        } catch (Exception e) {
            log.error("Error in cracServiceSummary API", e);
            throw new RuntimeException("Failed to fetch CRAC Service summary");
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getGoodCracDetails(RequestDto request) {

        try {
            LoginResponseDto token = login();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<RequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 2: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 3: Decrypt if encrypted
            if (root.has("data")) {

                String encryptedData = root.get("data").asText();

                String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

                log.info("Decrypted JSON: {}", decryptedJson);

                JsonNode decryptedNode = mapper.readTree(decryptedJson);

                // ✅ Step 4: Build FINAL RESPONSE
//                ObjectNode finalResponse = mapper.createObjectNode();
//
//                finalResponse.put("sub", decryptedNode.get("sub").asText());
//                finalResponse.put("aud", decryptedNode.get("aud").asText());
//                finalResponse.put("iss", decryptedNode.get("iss").asText());
//                finalResponse.set("data", decryptedNode.get("data"));

                return decryptedNode;
            }

            // ✅ Step 5: If already plain
            return root;

        } catch (Exception e) {
            log.error("Error in getCrac API", e);
            throw new RuntimeException("Failed to fetch CRAC data");
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public JsonNode getBillSummary(RequestDto request) {
        try {
            // ✅ Step 1: Headers (TOKEN)
            LoginResponseDto token = login();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", token.getToken());

            HttpEntity<RequestDto> entity =
                    new HttpEntity<>(request, headers);

            // ✅ Step 2: Call GeM API
            ResponseEntity<String> response = restTemplate.exchange(
                    GEM_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.info("Raw Response: {}", body);

            JsonNode root = mapper.readTree(body);

            // ✅ Step 3: Decrypt if encrypted
            if (root.has("data")) {

                String encryptedData = root.get("data").asText();

                String decryptedJson = AESUtil.decrypt(encryptedData, secretKey);

                log.info("Decrypted JSON: {}", decryptedJson);

                JsonNode decryptedNode = mapper.readTree(decryptedJson);

                // ✅ Step 4: Final Response Format
//                ObjectNode finalResponse = mapper.createObjectNode();
//
//                finalResponse.put("sub", decryptedNode.get("sub").asText());
//                finalResponse.put("aud", decryptedNode.get("aud").asText());
//                finalResponse.put("iss", decryptedNode.get("iss").asText());
//                finalResponse.set("data", decryptedNode.get("data"));

                return decryptedNode;
            }

            // ✅ Step 5: If already plain
            return root;

        } catch (Exception e) {
            log.error("Error in billSummary API", e);
            throw new RuntimeException("Failed to fetch Bill Summary");
        }
    }


    // 📄 INVOICE API CALL
    private String callInvoiceApi(InvoiceSummaryRequestDto requestDto, String token) {

        requestDto.setUser(requestDto.getUser());
        requestDto.setMethod("invoiceSummary");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ⚠️ Based on your curl → NOT Bearer
        headers.set("authorization", token);

        HttpEntity<InvoiceSummaryRequestDto> entity =
                new HttpEntity<>(requestDto, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(GEM_URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}