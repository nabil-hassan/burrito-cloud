package net.nh.burrito.entity.jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderJDBC {
    private Long id;
    @Builder.Default
    private List<BurritoJDBC> burritos = new ArrayList();
    @NotBlank(message = "Name is mandatory")
    private String orderName;
    @NotBlank(message = "Street is mandatory")
    private String street;
    @NotBlank(message = "Town is mandatory")
    private String town;
    @NotBlank(message = "County is mandatory")
    private String county;
    @NotBlank(message = "Postcode is mandatory")
    private String postcode;
    @NotBlank(message = "Card No is mandatory")
    private String creditCardNo;
    @NotBlank(message = "Expiry Date is mandatory")
    private String creditCardExpiryDate;
    @NotBlank(message = "CCV is mandatory")
    private String creditCardCCV;
}
