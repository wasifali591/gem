package in.grse.gem.dtos.responses;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String status;
    private String token;
}