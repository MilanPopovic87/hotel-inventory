package com.milanpopovic.hotelinventory.dto;

import com.milanpopovic.hotelinventory.entity.Booking;
import java.time.LocalDate;

public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long roomId;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // Constructor to map from Booking entity
    public BookingResponseDTO(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUser().getId();
        this.username = booking.getUser().getUsername();
        this.roomId = booking.getRoom().getId();
        this.roomName = booking.getRoom().getName();
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
    }

    // Empty constructor (needed for serialization/deserialization)
    public BookingResponseDTO() {}

    // ===== Getters and Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}
