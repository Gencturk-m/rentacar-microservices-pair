package com.kodlamaio.commonpackage.utils.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "")
    @Length(min = 16, max = 16, message = "The card number field cannot be blank.")
    private String cardNumber;

    @NotBlank(message = "Cardholder information cannot be blank.")
    @Length(min = 5, message = "Cardholder information must consist of at least 5 characters.")
    private String cardHolder;

    @NotNull(message = "Card expiration year cannot be blank.")
    @Min(value = 2023, message = "The expiration year of the card is invalid.")
    private int cardExpirationYear;

    @NotNull
    @Max(value = 12)
    @Min(value = 1)
    private int cardExpirationMonth;

    @NotBlank
    @Length(min = 3, max = 3, message = "The number must be 3 digits")
    private String cardCvv;
}