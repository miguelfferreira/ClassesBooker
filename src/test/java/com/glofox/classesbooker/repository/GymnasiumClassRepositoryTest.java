package com.glofox.classesbooker.repository;

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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GymnasiumClassRepositoryTest {

    @Autowired
    private GymnasiumClassRepository gymnasiumClassRepository;

    private GymnasiumClass gymnasiumClassInitializer() {
        Date startDate = Utils.getDateFromString("12-08-2023");
        Date endDate = Utils.getDateFromString("12-08-2023");
        return new GymnasiumClass(1L, "Pilates", startDate, endDate, 20, null);
    }

    private GymnasiumClass secondGymnasiumClassInitializer() {
        Date startDate = Utils.getDateFromString("13-08-2023");
        Date endDate = Utils.getDateFromString("13-08-2023");
        return new GymnasiumClass(2L, "Aerobics", startDate, endDate, 20, null);
    }

    private Pageable pageableInitializer() {
        SortDirEnum sortDir = SortDirEnum.asc;
        SortByEnum sortBy = SortByEnum.startDate;

        Sort sort = sortDir.toString().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy.toString()).ascending() : Sort.by(sortBy.toString()).descending();
        return PageRequest.of(0, 10, sort);
    }

    @Test
    public void shouldFindWithGivenName() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);

        Pageable pageable = pageableInitializer();

        Page<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByNameContaining("Pilates", pageable);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.getContent().get(0).getName()).isEqualTo(gymnasiumClass.getName());
    }

    @Test
    public void shouldNotFindWithGivenStartDate() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);

        Date date = Utils.getDateFromString("14-08-2023");
        Optional<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findByStartDate(date);

        assertThat(foundGymnasiumClass).isEmpty();
    }

    @Test
    public void shouldFindTwoWithGivenDatesInterval() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);
        GymnasiumClass secondGymnasiumClass = secondGymnasiumClassInitializer();
        gymnasiumClassRepository.save(secondGymnasiumClass);

        Date startDate = Utils.getDateFromString("12-08-2023");
        Date endDate = Utils.getDateFromString("13-08-2023");

        List<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.size()).isEqualTo(2);
    }

    @Test
    public void shouldFindOneWithGivenNameAndDatesInterval() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);
        GymnasiumClass secondGymnasiumClass = secondGymnasiumClassInitializer();
        gymnasiumClassRepository.save(secondGymnasiumClass);

        Date startDate = Utils.getDateFromString("12-08-2023");
        Date endDate = Utils.getDateFromString("13-08-2023");
        Pageable pageable = pageableInitializer();

        Page<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual("Pilates", startDate, endDate, pageable);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.getContent().size()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteGymnasiumClass() {
        GymnasiumClass gymnasiumClass = gymnasiumClassInitializer();
        gymnasiumClassRepository.save(gymnasiumClass);

        Pageable pageable = pageableInitializer();

        Page<GymnasiumClass> foundGymnasiumClass = gymnasiumClassRepository.findAllByNameContaining("Pilates", pageable);

        assertThat(foundGymnasiumClass).isNotNull();
        assertThat(foundGymnasiumClass.getContent().size()).isEqualTo(1);

        GymnasiumClass gymnasiumClassToDelete = foundGymnasiumClass.getContent().get(0);
        gymnasiumClassRepository.delete(gymnasiumClassToDelete);

        Page<GymnasiumClass> notFoundGymnasiumClass = gymnasiumClassRepository.findAllByNameContaining("Pilates", pageable);
        assertThat(notFoundGymnasiumClass).isEmpty();

    }
}
