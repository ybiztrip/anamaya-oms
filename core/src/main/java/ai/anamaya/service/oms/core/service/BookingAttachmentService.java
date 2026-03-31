package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingAttachmentRequest;
import ai.anamaya.service.oms.core.dto.response.BookingAttachmentResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingAttachment;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.repository.BookingAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAttachmentService {

    private final BookingAttachmentRepository bookingAttachmentRepository;
    private final BookingService bookingService;

    public List<BookingAttachmentResponse> submitBookingAttachments(CallerContext callerContext, Long bookingId, BookingAttachmentRequest request) throws IOException {
        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        List<BookingAttachment> attachments = request.getFiles().stream()
            .map(file -> BookingAttachment.builder()
                .companyId(companyId)
                .bookingId(bookingId)
                .bookingCode(booking.getCode())
                .type(BookingType.JOURNEY)
                .file(file)
                .createdBy(userId)
                .updatedBy(userId)
                .build()
            ).collect(Collectors.toList());

        bookingAttachmentRepository.saveAll(attachments);

        return attachments.stream()
            .map(this::toResponse)
            .toList();
    }

    private BookingAttachmentResponse toResponse(BookingAttachment data) {
        return BookingAttachmentResponse.builder()
            .id(data.getId())
            .companyId(data.getCompanyId())
            .bookingId(data.getBookingId())
            .bookingCode(data.getBookingCode())
            .type(data.getType())
            .file(data.getFile())
            .build();
    }

}
