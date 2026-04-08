package in.grse.gem.dtos.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDataDto {

    private String pgMode;
    private String orderId;
    private String orderDate;
    private String acceptedDate;
    private Double orderAmount;
    private String demandId;

    private String buyerOrg;
    private String buyerName;
    private String buyerEmail;
    private String buyerMobile;
    private String buyerAddress;
    private String buyerPincode;
    private String buyerDistrict;
    private String buyerState;
    private String buyerGstn;

    private String vendorName;
    private String vendorAddress;
    private String vendorCode;
    private String vendorDistrict;
    private String vendorState;
    private String vendorPin;
    private String vendorBankAccountNo;
    private String vendorBankIfscCode;
    private String vendorPan;
    private String vendorGstn;
    private String vendorUniqueId;

    private String sellerId;
    private String supplyOrderNo;
    private String supplyOrderDate;

    private String designationFinancial;
    private String ifdConcurrance;
    private String ifdDiaryNo;
    private String ifdDiaryDate;

    private List<ConsignmentDto> consignmentDetails;

    private String contractFile;
    private String amendedStatus;
    private String parentOrderId;

    private String buyerUserID;
}
