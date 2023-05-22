package com.kodlamaio.paymentservice.business.kafka.consume;

import com.kodlamaio.commonpackage.events.rental.RentalCreatedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalPaymentEvent;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.paymentservice.business.abstracts.PaymentService;
import com.kodlamaio.paymentservice.business.dto.requests.CreatePaymentRequest;
import com.kodlamaio.paymentservice.business.dto.requests.UpdatePaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "rental-payment-created",
            groupId = "payment-rental-create"
    )
    public void consume(RentalPaymentEvent rentalPaymentEvent) {
        paymentService.processRentalPayment(rentalPaymentEvent.getCreateRentalPaymentRequest());


        log.info("Rental created event consumed {}", rentalPaymentEvent);
    }
}
