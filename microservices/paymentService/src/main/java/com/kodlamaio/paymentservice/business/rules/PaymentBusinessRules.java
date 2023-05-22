package com.kodlamaio.paymentservice.business.rules;

import com.kodlamaio.commonpackage.utils.dto.CreateRentalPaymentRequest;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.paymentservice.business.dto.requests.CreatePaymentRequest;
import com.kodlamaio.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentBusinessRules {
    private final PaymentRepository paymentRepository;

    public void checkIfPaymentExists(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new BusinessException("There is no payment information found");
        }
    }

    public void checkIfBalanceIsEnough(double price, double balance) {
        if (balance < price) {
            throw new BusinessException("Insufficient balance.");
        }
    }

    public void checkIfCardExists(CreatePaymentRequest createPaymentRequest) {
        if (paymentRepository.existsByCardNumber(createPaymentRequest.getCardNumber())) {
            throw new BusinessException("The card number is already registered.");
        }
    }

    public void checkIfPaymentIsValid(CreateRentalPaymentRequest createRentalPaymentRequest) {
        if (!paymentRepository.existsByCardNumberAndCardHolderAndCardExpirationYearAndCardExpirationMonthAndCardCvv(
                createRentalPaymentRequest.getCardNumber(),
                createRentalPaymentRequest.getCardHolder(),
                createRentalPaymentRequest.getCardExpirationYear(),
                createRentalPaymentRequest.getCardExpirationMonth(),
                createRentalPaymentRequest.getCardCvv()
        )) {
            throw new RuntimeException("Your card details are incorrect.");
        }
    }
}
