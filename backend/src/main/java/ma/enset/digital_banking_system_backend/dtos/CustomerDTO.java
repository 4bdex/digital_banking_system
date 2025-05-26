package ma.enset.digital_banking_system_backend.dtos;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String createdBy;
}
