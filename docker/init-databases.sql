-- Initialize separate databases for each microservice
-- This script runs automatically when the PostgreSQL container starts for the first time.

CREATE DATABASE bookflex_users;
CREATE DATABASE bookflex_resources;
CREATE DATABASE bookflex_bookings;
CREATE DATABASE bookflex_payments;
CREATE DATABASE bookflex_notifications;
