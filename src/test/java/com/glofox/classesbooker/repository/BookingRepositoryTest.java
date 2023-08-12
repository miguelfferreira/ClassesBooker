package com.glofox.classesbooker.repository;

import com.glofox.classesbooker.entity.Booking;
import com.glofox.classesbooker.entity.GymnasiumClass;
import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.utils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    private GymnasiumClassRepository gymnasiumClassRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private GymnasiumClass gymnasiumClassInitializer() {
        Date startDate = Utils.getDateFromString("12-08-2023");
        Date endDate = Utils.getDateFromString("12-08-2023");
        return new GymnasiumClass(1L, "Pilates", startDate, endDate, 20, null);
    }

    private Pageable pageableInitializer() {
        SortDirEnum sortDir = SortDirEnum.asc;
        SortByEnum sortBy = SortByEnum.startDate;

        Sort sort = sortDir.toString().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy.toString()).ascending() : Sort.by(sortBy.toString()).descending();
        return PageRequest.of(0, 10, sort);
    }

    @Test
    public void addBook() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);

        Booking booking = new Booking();
        booking.setMemberName("Miguel");
        booking.setDate(Utils.getDateFromString("12-08-2023"));
        booking.setGymnasiumClass(gymnasiumClass);
        bookingRepository.save(booking);

        Optional<Booking> bookingToAdd = bookingRepository.findByMemberNameIgnoreCaseAndDate(booking.getMemberName(), booking.getDate());

        assertThat(bookingToAdd).isPresent();
        assertThat(bookingToAdd.get().getMemberName()).isEqualTo(booking.getMemberName());

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingToAdd.get());
        gymnasiumClass.setBookings(bookings);
        gymnasiumClassRepository.save(gymnasiumClass);

        Pageable pageable = pageableInitializer();

        Page<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByNameContaining("Pilates", pageable);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.getContent().get(0).getBookings().size()).isEqualTo(1);
    }

    @Test
    public void deleteBook() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);

        Booking booking = new Booking();
        booking.setMemberName("Miguel");
        booking.setDate(Utils.getDateFromString("12-08-2023"));
        booking.setGymnasiumClass(gymnasiumClass);
        bookingRepository.save(booking);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        gymnasiumClass.setBookings(bookings);
        gymnasiumClassRepository.save(gymnasiumClass);

        Optional<Booking> bookingToDelete = bookingRepository.findByMemberNameIgnoreCaseAndDate(booking.getMemberName(), booking.getDate());
        assertThat(bookingToDelete).isPresent();

        gymnasiumClass.getBookings().clear();
        gymnasiumClassRepository.save(gymnasiumClass);

        Pageable pageable = pageableInitializer();
        
        Page<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByNameContaining("Pilates", pageable);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.getContent().get(0).getBookings().size()).isEqualTo(0);
    }
}
