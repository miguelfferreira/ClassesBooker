package com.glofox.classesbooker.service.impl;

import com.glofox.classesbooker.entity.Booking;
import com.glofox.classesbooker.entity.GymnasiumClass;
import com.glofox.classesbooker.exception.BadRequestException;
import com.glofox.classesbooker.exception.ForbiddenException;
import com.glofox.classesbooker.exception.ResourceNotFoundException;
import com.glofox.classesbooker.payload.BookingDto;
import com.glofox.classesbooker.repository.BookingRepository;
import com.glofox.classesbooker.repository.GymnasiumClassRepository;
import com.glofox.classesbooker.service.BookingService;
import com.glofox.classesbooker.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GymnasiumClassRepository gymnasiumClassRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, GymnasiumClassRepository gymnasiumClassRepository) {
        this.bookingRepository = bookingRepository;
        this.gymnasiumClassRepository = gymnasiumClassRepository;
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        BookingDto newBookingDto = new BookingDto();

        if (basicBookingValidations(bookingDto)) {
            if (Utils.isDateFormatInvalid(bookingDto.getDate(), null)) {
                throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
            }

            Date date = Utils.getDateFromString(bookingDto.getDate());

            GymnasiumClass gymnasiumClass = gymnasiumClassRepository.findByStartDate(date).orElseThrow(() -> new ResourceNotFoundException("There is no class on the given date, hence booking is not available"));

            if (bookingAlreadyExists(bookingDto.getMemberName(), date)) {
                throw new ForbiddenException("Booking already exists");
            }

            Booking booking = new Booking();
            booking.setMemberName(bookingDto.getMemberName());
            booking.setDate(date);
            booking.setGymnasiumClass(gymnasiumClass);
            Booking newBooking = bookingRepository.save(booking);

            gymnasiumClass.setCapacity(gymnasiumClass.getCapacity() - 1);
            gymnasiumClassRepository.save(gymnasiumClass);

            newBookingDto.setMemberName(newBooking.getMemberName());
            newBookingDto.setDate(Utils.getStringFromDate(newBooking.getDate()));
        }
        return newBookingDto;
    }

    @Override
    public void deleteBooking(String memberName, String date) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setMemberName(memberName);
        bookingDto.setDate(date);
        if (basicBookingValidations(bookingDto)) {
            if (Utils.isDateFormatInvalid(bookingDto.getDate(), null)) {
                throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
            }
            Booking booking = bookingRepository.findByMemberNameIgnoreCaseAndDate(memberName, Utils.getDateFromString(date)).orElseThrow(() -> new ResourceNotFoundException("No booking found for the given member on the given date"));
            bookingRepository.delete(booking);
        }
    }

    private boolean basicBookingValidations(BookingDto bookingDto) {
        String errorValue = null;
        boolean isValid = true;
        if (StringUtils.isBlank(bookingDto.getMemberName())) {
            errorValue = "name";
            isValid = false;
        } else if (StringUtils.isBlank(bookingDto.getDate())) {
            errorValue = "date";
            isValid = false;
        }
        if (!isValid) {
            throw new BadRequestException("Missing input field: " + errorValue);
        }
        return isValid;
    }

    private boolean bookingAlreadyExists(String memberName, Date date) {
        return bookingRepository.findByMemberNameIgnoreCaseAndDate(memberName, date).isPresent();
    }
}
