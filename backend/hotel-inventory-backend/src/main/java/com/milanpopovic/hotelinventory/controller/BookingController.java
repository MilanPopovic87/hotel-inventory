package com.milanpopovic.hotelinventory.controller;

import com.milanpopovic.hotelinventory.dto.BookingRequestDTO;
import com.milanpopovic.hotelinventory.dto.BookingResponseDTO;
import com.milanpopovic.hotelinventory.entity.Booking;
import com.milanpopovic.hotelinventory.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Get all bookings
    @GetMapping
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.getAllBookings().stream()
                .map(BookingResponseDTO::new)
                .toList();
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public BookingResponseDTO getBookingById(@PathVariable Long id) {
        return new BookingResponseDTO(
                bookingService.getBookingById(id)
        );
    }

    // Get bookings by user
    @GetMapping("/by-user/{userId}")
    public List<BookingResponseDTO> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId).stream()
                .map(BookingResponseDTO::new)
                .toList();
    }

    // Get bookings by room
    @GetMapping("/by-room/{roomId}")
    public List<BookingResponseDTO> getBookingsByRoom(@PathVariable Long roomId) {
        return bookingService.getBookingsByRoomId(roomId).stream()
                .map(BookingResponseDTO::new)
                .toList();
    }

    // Create a booking
    @PostMapping
    public BookingResponseDTO create(@RequestBody BookingRequestDTO dto) {
        Booking booking = bookingService.createBooking(dto);
        return new BookingResponseDTO(booking);
    }

    // Update a booking
    // Reserved for future admin booking edits
    @PutMapping("/{id}")
    public BookingResponseDTO updateBooking(
            @PathVariable Long id,
            @RequestBody BookingRequestDTO dto
    ) {
        Booking booking = bookingService.updateBooking(id, dto);
        return new BookingResponseDTO(booking);
    }

    // Delete a booking
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
