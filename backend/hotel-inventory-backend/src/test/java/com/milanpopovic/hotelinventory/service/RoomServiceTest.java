package com.milanpopovic.hotelinventory.service;

import com.milanpopovic.hotelinventory.entity.Booking;
import com.milanpopovic.hotelinventory.entity.Room;
import com.milanpopovic.hotelinventory.entity.User;
import com.milanpopovic.hotelinventory.repository.BookingRepository;
import com.milanpopovic.hotelinventory.repository.RoomRepository;
import com.milanpopovic.hotelinventory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldNotDeleteRoomWithBookings() {
        // Arrange
        Room room = roomRepository.save(
                new Room("Test Room", "STANDARD", BigDecimal.valueOf(100), true)
        );

        User user = userRepository.save(
                new User("testuser", "password", "USER")
        );

        bookingRepository.save(
                new Booking(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3),
                        room,
                        user
                )
        );

        // Act + Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> roomService.deleteRoom(room.getId()));

        assertEquals(400, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Room has bookings and cannot be deleted"));
    }
}
