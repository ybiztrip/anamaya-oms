package ai.anamaya.service.oms.core.service;

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
    public BookingResponse updateBookingPax(Long bookingId, List<BookingPaxRequest> paxRequests) {
        Long userId = jwtUtils.getUserIdFromToken();

        Booking booking = bookingService.getValidatedBooking(bookingId);

        if (booking.getStatus() != BookingStatus.DRAFT) {
            throw new AccessDeniedException("This booking can no longer be updated.");
        }

        paxRequests.forEach(request -> {
            if (request.getId() != null) {
                bookingPaxRepository.findById(request.getId()).ifPresent(existingPax -> {
                    if (request.isDeleted()) {
                        bookingPaxRepository.delete(existingPax);
                    } else {
                        existingPax.setFirstName(request.getFirstName());
                        existingPax.setLastName(request.getLastName());
                        existingPax.setTitle(request.getTitle());
                        existingPax.setEmail(request.getEmail());
                        existingPax.setGender(request.getGender());
                        existingPax.setType(request.getType());
                        existingPax.setNationality(request.getNationality());
                        existingPax.setPhoneCode(request.getPhoneCode());
                        existingPax.setPhoneNumber(request.getPhoneNumber());
                        existingPax.setDob(request.getDob());
                        existingPax.setAddOn(request.getAddOn());
                        existingPax.setIssuingCountry(request.getIssuingCountry());
                        existingPax.setDocumentType(request.getDocumentType());
                        existingPax.setDocumentNo(request.getDocumentNo());
                        existingPax.setExpirationDate(request.getExpirationDate());
                        existingPax.setUpdatedBy(userId);
                        bookingPaxRepository.save(existingPax);
                    }
                });
            } else {
                BookingPax newPax = BookingPax.builder()
                        .bookingId(bookingId)
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
            }
        });

        return bookingService.toResponse(booking, true, false);
    }

}
