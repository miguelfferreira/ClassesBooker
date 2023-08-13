package com.glofox.classesbooker.repository;

import com.glofox.classesbooker.entity.GymnasiumClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GymnasiumClassRepository extends JpaRepository<GymnasiumClass, Long> {

    Page<GymnasiumClass> findAllByNameContaining(String name, Pageable page);

    Page<GymnasiumClass> findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(Date startDate, Date endDate, Pageable pageable);

    Page<GymnasiumClass> findAllByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String name, Date startDate, Date endDate, Pageable page);

    Optional<GymnasiumClass> findByStartDate(Date startDate);

    List<GymnasiumClass> findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(Date startDate, Date endDate);

    //Iterable<GymnasiumClass> findAllByNameContaining(String name);
}
