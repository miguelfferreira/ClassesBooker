package com.glofox.classesbooker.service;

import com.glofox.classesbooker.payload.BookingDto;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto);

    void deleteBooking(String memberName, String date);
}
