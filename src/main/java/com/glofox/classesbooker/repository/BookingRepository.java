package com.glofox.classesbooker.repository;

import com.glofox.classesbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByMemberNameIgnoreCaseAndDate(String memberName, Date startDate);

}
