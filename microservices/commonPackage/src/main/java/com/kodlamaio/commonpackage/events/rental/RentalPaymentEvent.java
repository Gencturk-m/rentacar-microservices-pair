package com.kodlamaio.commonpackage.events.rental;

import com.kodlamaio.commonpackage.events.Event;
import com.kodlamaio.commonpackage.utils.dto.CreateRentalPaymentRequest;
import com.kodlamaio.commonpackage.utils.dto.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RentalPaymentEvent implements Event {
   private CreateRentalPaymentRequest createRentalPaymentRequest;
}
