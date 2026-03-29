package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingAttachmentRequest;
import ai.anamaya.service.oms.core.dto.request.DocumentUploadRequest;
import ai.anamaya.service.oms.core.dto.response.BookingAttachmentResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingAttachment;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.DocumentBucketType;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.repository.BookingAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAttachmentService {

    private final BookingAttachmentRepository bookingAttachmentRepository;
    private final BookingService bookingService;
    private final DocumentService documentService;

    public BookingAttachmentResponse submitBookingAttachments(CallerContext callerContext, Long bookingId, BookingAttachmentRequest request) throws IOException {
        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        DocumentUploadRequest documentUploadRequest = DocumentUploadRequest.builder()
            .type(DocumentBucketType.ATTACHMENT_BOOKING)
            .file(request.getFile())
            .build();
        String file = documentService.uploadFile(callerContext, documentUploadRequest);

        BookingAttachment bookingAttachment = BookingAttachment.builder()
            .companyId(companyId)
            .bookingId(bookingId)
            .bookingCode(booking.getCode())
            .type(BookingType.JOURNEY)
            .file(file)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        bookingAttachmentRepository.save(bookingAttachment);

        return this.toResponse(bookingAttachment);
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
