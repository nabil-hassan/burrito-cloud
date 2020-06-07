package net.nh.burrito.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Order {
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
