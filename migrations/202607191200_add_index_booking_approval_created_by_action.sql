CREATE INDEX idx_ba_created_by_action
ON booking_approval (created_by, action, booking_id);
