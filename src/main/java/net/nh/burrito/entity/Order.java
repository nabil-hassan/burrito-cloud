package net.nh.burrito.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    private Long id;
    @Builder.Default
    private List<Burrito> burritos = new ArrayList();
    @NotBlank(message = "Name is mandatory")
    private String name;
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
