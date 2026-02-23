package com.milanpopovic.hotelinventory.service;

import com.milanpopovic.hotelinventory.dto.BookingRequestDTO;
import com.milanpopovic.hotelinventory.entity.Booking;
import com.milanpopovic.hotelinventory.entity.Room;
import com.milanpopovic.hotelinventory.entity.User;
import com.milanpopovic.hotelinventory.repository.BookingRepository;
import com.milanpopovic.hotelinventory.repository.RoomRepository;
import com.milanpopovic.hotelinventory.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    // ================= READ =================

    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    // ================= CREATE =================

    /**
     * Creates a new booking.
     * Uses @Transactional to prevent race conditions (double booking).
     */

    @PreAuthorize("hasRole('ADMIN') or #dto.userId == authentication.principal.id")
    @Transactional
    public Booking createBooking(BookingRequestDTO dto) {

        Room room = getRoom(dto.getRoomId());
        User user = getUser(dto.getUserId());

        validateBookingDates(dto.getCheckInDate(), dto.getCheckOutDate());
        checkOverlap(room, dto.getCheckInDate(), dto.getCheckOutDate(), null);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());

        return bookingRepository.save(booking);
    }

    // ================= UPDATE =================

    /**
     * Updates an existing booking.
     */
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    @Transactional
    public Booking updateBooking(Long id, BookingRequestDTO dto) {

        Booking booking = getBookingById(id);
        Room room = getRoom(dto.getRoomId());
        User user = getUser(dto.getUserId());

        validateBookingDates(dto.getCheckInDate(), dto.getCheckOutDate());
        checkOverlap(room, dto.getCheckInDate(), dto.getCheckOutDate(), booking.getId());

        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());

        return bookingRepository.save(booking);
    }

    // ================= DELETE =================

    /**
     * Deletes a booking by id.
     * If booking does not exist, operation is silently ignored.
     */
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ============================================================
    // ================= HELPER METHODS (PRIVATE) =================
    // ============================================================

    /**
     * Helper method to check ownership
     */
    public boolean isOwner(Long bookingId, String username) {
        return bookingRepository.findById(bookingId)
                .map(b -> b.getUser().getUsername().equals(username))
                .orElse(false);
    }

    /**
     * Fetch room or throw 404 error.
     */
    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found"
                ));
    }

    /**
     * Fetch user or throw 404 error.
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    /**
     * Validates booking dates (business rules).
     */
    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {

        // Dates must not be null
        if (checkIn == null || checkOut == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in and check-out dates are required"
            );
        }

        // Check-out must be after check-in
        if (!checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date must be after check-in date"
            );
        }

        // Booking cannot be more than 1 year in advance
        LocalDate maxAllowed = LocalDate.now().plusYears(1);
        if (checkIn.isAfter(maxAllowed) || checkOut.isAfter(maxAllowed)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking cannot be more than 1 year in advance"
            );
        }

        // Booking cannot start in the past
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking cannot start in the past"
            );
        }
    }

    /**
     * Checks if there is an overlapping booking for the same room.
     * excludeBookingId is used when updating an existing booking.
     */
    private void checkOverlap(Room room, LocalDate checkIn, LocalDate checkOut, Long excludeBookingId) {

        boolean exists;

        if (excludeBookingId == null) {
            // CREATE case
            exists = bookingRepository.existsByRoomAndDateOverlap(room, checkIn, checkOut);
        } else {
            // UPDATE case
            exists = bookingRepository.existsByRoomAndDateOverlapExcludingBooking(
                    room,
                    checkIn,
                    checkOut,
                    excludeBookingId
            );
        }

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room already booked for these dates"
            );
        }
    }
}
