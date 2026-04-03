package com.milanpopovic.hotelinventory.repository;

import com.milanpopovic.hotelinventory.entity.Booking;
import com.milanpopovic.hotelinventory.entity.Room;
import com.milanpopovic.hotelinventory.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnTrueWhenRoomHasBookings() {
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

        // Act
        boolean exists = bookingRepository.existsByRoomId(room.getId());

        // Assert
        assertTrue(exists);
    }
}

