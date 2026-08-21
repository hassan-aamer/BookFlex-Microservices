package com.bookflex.booking.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CancelBookingRequest {
    private String reason;
}
