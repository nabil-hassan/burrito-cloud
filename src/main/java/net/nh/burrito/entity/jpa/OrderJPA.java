package net.nh.burrito.entity.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class OrderJPA implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @ManyToMany(targetEntity= BurritoJPA.class, fetch = FetchType.EAGER)
    @JoinTable(name = "order_burritos",
            joinColumns = @JoinColumn(name = "order_id", updatable = false, nullable = false),
            inverseJoinColumns = @JoinColumn(name = "burrito_id", updatable = false, nullable = false))
    private List<BurritoJPA> burritos = new ArrayList();

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
    @Column(name = "ccNo")
    private String creditCardNo;

    @NotBlank(message = "Expiry Date is mandatory")
    @Column(name = "ccExpiryDate")
    private String creditCardExpiryDate;

    @NotBlank(message = "CCV is mandatory")
    @Column(name = "ccCCV")
    private String creditCardCCV;

    private Date placedAt;

    @PrePersist
    void placedAt() {
        this.placedAt = new Date();
    }

}
