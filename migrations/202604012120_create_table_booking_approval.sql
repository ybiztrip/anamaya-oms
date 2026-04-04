CREATE TABLE booking_approval (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    booking_type VARCHAR(20) NOT NULL,  -- BOOKING / FLIGHT / HOTEL
    booking_child_id BIGINT NULL,
    action VARCHAR(20) NOT NULL,     -- APPROVED / REJECTED
    notes TEXT,
    created_by BIGINT NOT NULL,      -- userId
    created_by_name VARCHAR(255) NULL,  -- snapshot (email)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);