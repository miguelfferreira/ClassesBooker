package com.glofox.classesbooker.controller;

import com.glofox.classesbooker.payload.BookingDto;
import com.glofox.classesbooker.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Bookings Controller Endpoint")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Creates a booking on a given class, on a given date (date format must be dd-MM-yyyy)")
    @PostMapping()
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        return new ResponseEntity<>(bookingService.createBooking(bookingDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Booking for the given member, on the given date")
    @DeleteMapping()
    public ResponseEntity<String> deleteBooking(@RequestParam(name = "memberName") String memberName,
                                                @RequestParam(name = "date") String date) {
        bookingService.deleteBooking(memberName, date);
        return new ResponseEntity<>("Booking deleted successfully.", HttpStatus.OK);
    }

}
