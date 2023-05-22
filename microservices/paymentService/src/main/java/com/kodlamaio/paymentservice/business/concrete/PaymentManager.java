package com.kodlamaio.paymentservice.business.concrete;

import com.kodlamaio.commonpackage.utils.dto.CreateRentalPaymentRequest;
import com.kodlamaio.paymentservice.adapters.PosService;
import com.kodlamaio.paymentservice.business.abstracts.PaymentService;
import com.kodlamaio.paymentservice.business.dto.requests.CreatePaymentRequest;
import com.kodlamaio.paymentservice.business.dto.requests.UpdatePaymentRequest;
import com.kodlamaio.paymentservice.business.dto.responses.CreatePaymentResponse;
import com.kodlamaio.paymentservice.business.dto.responses.GetAllPaymentsResponse;
import com.kodlamaio.paymentservice.business.dto.responses.GetPaymentResponse;
import com.kodlamaio.paymentservice.business.dto.responses.UpdatePaymentResponse;
import com.kodlamaio.paymentservice.business.rules.PaymentBusinessRules;
import com.kodlamaio.paymentservice.entities.Payment;
import com.kodlamaio.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentManager implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final PaymentBusinessRules paymentBusinessRules;
    private final PosService posService;
    @Override
    public List<GetAllPaymentsResponse> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        List<GetAllPaymentsResponse> getAllPaymentsResponses = payments
                .stream()
                .map(payment -> modelMapper.map(payment, GetAllPaymentsResponse.class))
                .toList();

        return getAllPaymentsResponses;
    }

    @Override
    public GetPaymentResponse getById(UUID id) {
        paymentBusinessRules.checkIfPaymentExists(id);
        Payment payment = paymentRepository.findById(id).orElseThrow();
        GetPaymentResponse getPaymentResponse = modelMapper.map(payment, GetPaymentResponse.class);

        return getPaymentResponse;
    }

    @Override
    public CreatePaymentResponse add(CreatePaymentRequest createPaymentRequest) {
        paymentBusinessRules.checkIfCardExists(createPaymentRequest);
        Payment payment = modelMapper.map(createPaymentRequest, Payment.class);
        payment.setId(UUID.randomUUID());
        paymentRepository.save(payment);
        CreatePaymentResponse createPaymentResponse
                = modelMapper.map(payment, CreatePaymentResponse.class);

        return createPaymentResponse;
    }

    @Override
    public UpdatePaymentResponse update(UpdatePaymentRequest updatePaymentRequest) {
        paymentBusinessRules.checkIfPaymentExists(updatePaymentRequest.getId());
        Payment payment = modelMapper.map(updatePaymentRequest, Payment.class);
        paymentRepository.save(payment);
        UpdatePaymentResponse updatePaymentResponse = modelMapper.map(payment, UpdatePaymentResponse.class);

        return updatePaymentResponse;
    }

    @Override
    public void delete(UUID id) {
        paymentBusinessRules.checkIfPaymentExists(id);
        paymentRepository.deleteById(id);
    }

    @Override
    public void processRentalPayment(CreateRentalPaymentRequest createRentalPaymentRequest) {
        paymentBusinessRules.checkIfPaymentIsValid(createRentalPaymentRequest);
        Payment payment = paymentRepository.findByCardNumber(createRentalPaymentRequest.getCardNumber());
        paymentBusinessRules.checkIfBalanceIsEnough(createRentalPaymentRequest.getPrice(), payment.getBalance());
        posService.pay();
        payment.setBalance(payment.getBalance() - createRentalPaymentRequest.getPrice());
        paymentRepository.save(payment);
    }
}
