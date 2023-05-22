package com.kodlamaio.paymentservice.business.dto.requests;

import com.kodlamaio.commonpackage.utils.dto.PaymentRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentRequest extends PaymentRequest {

    private UUID id;

    @NotNull
    @Min(value = 1)
    private double balance;

}