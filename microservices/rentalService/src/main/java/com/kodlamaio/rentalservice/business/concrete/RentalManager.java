package com.kodlamaio.rentalservice.business.concrete;

import com.kodlamaio.commonpackage.events.rental.RentalCreatedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalDeletedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalPaymentEvent;
import com.kodlamaio.commonpackage.kafka.KafkaProducer;
import com.kodlamaio.commonpackage.utils.dto.CreateRentalPaymentRequest;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.rentalservice.business.abstracts.RentalService;
import com.kodlamaio.rentalservice.business.dto.requests.CreateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.requests.UpdateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.responses.CreateRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.GetAllRentalsResponse;
import com.kodlamaio.rentalservice.business.dto.responses.GetRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.UpdateRentalResponse;
import com.kodlamaio.rentalservice.business.rules.RentalBusinessRules;
import com.kodlamaio.rentalservice.entities.Rental;
import com.kodlamaio.rentalservice.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalManager implements RentalService {
    private final RentalRepository rentalRepository;
    private final ModelMapperService modelMapperService;
    private final RentalBusinessRules rentalBusinessRules;
    private final KafkaProducer kafkaProducer;
    @Override
    public List<GetAllRentalsResponse> getAll() {
        var rentals = rentalRepository.findAll();
        var response = rentals
                .stream()
                .map(rental -> modelMapperService.forResponse().map(rental, GetAllRentalsResponse.class))
                .toList();

        return response;
    }

    @Override
    public GetRentalResponse getById(UUID id) {
        rentalBusinessRules.checkIfRentalExists(id);
        var rental = rentalRepository.findById(id).orElseThrow();
        var response = modelMapperService.forResponse().map(rental, GetRentalResponse.class);

        return response;
    }

    @Override
    public CreateRentalResponse add(CreateRentalRequest createRentalRequest) {
        rentalBusinessRules.ensureCarIsAvailable(createRentalRequest.getCarId());
        var rental = modelMapperService.forRequest().map(createRentalRequest, Rental.class);
        rental.setId(null);
        rental.setTotalPrice(getTotalPrice(rental));
        rental.setRentedAt(LocalDate.now());

        CreateRentalPaymentRequest createRentalPaymentRequest = new CreateRentalPaymentRequest();
        modelMapperService.forRequest().map(createRentalRequest.getPaymentRequest(), createRentalPaymentRequest);
        createRentalPaymentRequest.setPrice(getTotalPrice(rental));
        SendKafkaRentalPaymentEvent(createRentalPaymentRequest);
        
        rentalRepository.save(rental);
        sendKafkaRentalCreatedEvent(createRentalRequest.getCarId());

        var response = modelMapperService.forResponse().map(rental, CreateRentalResponse.class);

        return response;
    }
    @Override
    public UpdateRentalResponse update(UpdateRentalRequest updateRentalRequest) {
        rentalBusinessRules.checkIfRentalExists(updateRentalRequest.getId());
        var rental = modelMapperService.forRequest().map(updateRentalRequest, Rental.class);
        rentalRepository.save(rental);
        var response = modelMapperService.forResponse().map(rental, UpdateRentalResponse.class);

        return response;
    }

    @Override
    public void delete(UUID id) {
        rentalBusinessRules.checkIfRentalExists(id);
        sendKafkaRentalDeletedEvent(id);
        rentalRepository.deleteById(id);
    }
    private double getTotalPrice(Rental rental) {
        return rental.getDailyPrice() * rental.getRentedForDays();
    }
    private void sendKafkaRentalCreatedEvent(UUID carId){
        kafkaProducer.sendMessage(new RentalCreatedEvent(carId), "rental-created");
    }
    private void sendKafkaRentalDeletedEvent(UUID id){
        var carId = rentalRepository.findById(id).orElseThrow().getCarId();
        kafkaProducer.sendMessage(new RentalDeletedEvent(carId), "rental-deleted");
    }
    private void SendKafkaRentalPaymentEvent(CreateRentalPaymentRequest createRentalPaymentRequest) {
        kafkaProducer.sendMessage(new RentalPaymentEvent(createRentalPaymentRequest),
                "rental-payment-created");
    }
}
