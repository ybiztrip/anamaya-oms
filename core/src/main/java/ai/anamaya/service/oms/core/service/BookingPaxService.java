package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingPaxRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingPaxService {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public Void submitBookingPax(
        CallerContext callerContext,
        Long bookingId,
        String bookingCode,
        List<BookingPaxRequest> paxRequests
    ) {
        Long userId = callerContext.userId();

        paxRequests.forEach(request -> {

            BookingPax newPax = BookingPax.builder()
                .bookingId(bookingId)
                .bookingCode(bookingCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .title(request.getTitle())
                .email(request.getEmail())
                .gender(request.getGender())
                .type(request.getType())
                .nationality(request.getNationality())
                .phoneCode(request.getPhoneCode())
                .phoneNumber(request.getPhoneNumber())
                .dob(request.getDob())
                .addOn(request.getAddOn())
                .issuingCountry(request.getIssuingCountry())
                .documentType(request.getDocumentType())
                .documentNo(request.getDocumentNo())
                .expirationDate(request.getExpirationDate())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

            bookingPaxRepository.save(newPax);
        });

        return null;
    }

}
