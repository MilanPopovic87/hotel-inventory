package com.milanpopovic.hotelinventory.service;

import com.milanpopovic.hotelinventory.dto.BookingRequestDTO;
import com.milanpopovic.hotelinventory.entity.Room;
import com.milanpopovic.hotelinventory.entity.User;
import com.milanpopovic.hotelinventory.repository.RoomRepository;
import com.milanpopovic.hotelinventory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private Room createRoom() {
        Room room = new Room(
                "Test Room",
                "STANDARD",
                BigDecimal.valueOf(100),
                true
        );
        return roomRepository.save(room);
    }

    private User createUser() {
        User user = new User(
                "testuser",
                "password",
                "USER"
        );
        return userRepository.save(user);
    }

    @Test
    void shouldCreateBookingSuccessfully() {

        Room room = createRoom();
        User user = createUser();

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(room.getId());
        dto.setUserId(user.getId());
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));

        var booking = bookingService.createBooking(dto);

        assertNotNull(booking.getId());
        assertEquals(room.getId(), booking.getRoom().getId());
        assertEquals(user.getId(), booking.getUser().getId());
    }

    @Test
    void shouldRejectBookingInPast() {

        Room room = createRoom();
        User user = createUser();

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(room.getId());
        dto.setUserId(user.getId());
        dto.setCheckInDate(LocalDate.now().minusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(2));

        assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(dto));
    }

    @Test
    void shouldRejectInvalidDateRange() {

        Room room = createRoom();
        User user = createUser();

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(room.getId());
        dto.setUserId(user.getId());
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));

        assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(dto));
    }

    @Test
    void shouldRejectOverlappingBooking() {

        Room room = createRoom();
        User user = createUser();

        BookingRequestDTO first = new BookingRequestDTO();
        first.setRoomId(room.getId());
        first.setUserId(user.getId());
        first.setCheckInDate(LocalDate.now().plusDays(1));
        first.setCheckOutDate(LocalDate.now().plusDays(3));

        bookingService.createBooking(first);

        BookingRequestDTO second = new BookingRequestDTO();
        second.setRoomId(room.getId());
        second.setUserId(user.getId());
        second.setCheckInDate(LocalDate.now().plusDays(2));
        second.setCheckOutDate(LocalDate.now().plusDays(4));

        assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(second));
    }
}
