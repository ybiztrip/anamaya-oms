package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BookingPaxRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.entity.BookingPax;
import ai.anamaya.service.oms.exception.AccessDeniedException;
import ai.anamaya.service.oms.repository.BookingPaxRepository;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingPaxService {

    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public ApiResponse<BookingResponse> updateBookingPax(Long bookingId, List<BookingPaxRequest> paxRequests) {
        Long userId = jwtUtils.getUserIdFromToken();
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
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

        var paxList = bookingPaxRepository.findByBookingId(bookingId).stream()
                .map(this::toPaxResponse)
                .collect(Collectors.toList());

        var bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .companyId(booking.getCompanyId())
                .code(booking.getCode())
                .journeyCode(booking.getJourneyCode())
                .contactEmail(booking.getContactEmail())
                .contactFirstName(booking.getContactFirstName())
                .contactLastName(booking.getContactLastName())
                .contactTitle(booking.getContactTitle())
                .contactNationality(booking.getContactNationality())
                .contactPhoneCode(booking.getContactPhoneCode())
                .contactPhoneNumber(booking.getContactPhoneNumber())
                .contactDob(booking.getContactDob())
                .additionalInfo(booking.getAdditionalInfo())
                .clientAdditionalInfo(booking.getClientAdditionalInfo())
                .status(booking.getStatus())
                .passengers(paxList)
                .build();

        return ApiResponse.success(bookingResponse);
    }

    private BookingPaxResponse toPaxResponse(BookingPax pax) {
        return BookingPaxResponse.builder()
                .id(pax.getId())
                .bookingId(pax.getBookingId())
                .email(pax.getEmail())
                .firstName(pax.getFirstName())
                .lastName(pax.getLastName())
                .type(pax.getType())
                .title(pax.getTitle())
                .nationality(pax.getNationality())
                .phoneCode(pax.getPhoneCode())
                .phoneNumber(pax.getPhoneNumber())
                .dob(pax.getDob())
                .addOn(pax.getAddOn())
                .issuingCountry(pax.getIssuingCountry())
                .documentType(pax.getDocumentType())
                .documentNo(pax.getDocumentNo())
                .expirationDate(pax.getExpirationDate())
                .build();
    }
}
