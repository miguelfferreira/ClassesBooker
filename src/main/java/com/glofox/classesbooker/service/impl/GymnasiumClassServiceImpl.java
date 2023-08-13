package com.glofox.classesbooker.service.impl;

import com.glofox.classesbooker.entity.Booking;
import com.glofox.classesbooker.entity.GymnasiumClass;
import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.exception.BadRequestException;
import com.glofox.classesbooker.exception.ForbiddenException;
import com.glofox.classesbooker.exception.ResourceNotFoundException;
import com.glofox.classesbooker.payload.BookingDto;
import com.glofox.classesbooker.payload.GymnasiumClassDto;
import com.glofox.classesbooker.payload.GymnasiumClassPaginated;
import com.glofox.classesbooker.payload.GymnasiumClassResponseDto;
import com.glofox.classesbooker.repository.GymnasiumClassRepository;
import com.glofox.classesbooker.service.GymnasiumClassService;
import com.glofox.classesbooker.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GymnasiumClassServiceImpl implements GymnasiumClassService {

    @Autowired
    private GymnasiumClassRepository gymnasiumClassRepository;


    public GymnasiumClassServiceImpl(GymnasiumClassRepository gymnasiumClassRepository) {
        this.gymnasiumClassRepository = gymnasiumClassRepository;
    }

    @Override
    public GymnasiumClassPaginated getAllClasses(int pageNo, int pageSize, SortByEnum sortBy, SortDirEnum sortDir, String name, String startDateStr, String endDateStr) {
        if (Utils.isDateFormatInvalid(startDateStr, endDateStr)) {
            throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
        }

        if (sortDir == null) {
            sortDir = SortDirEnum.ASC;
        }
        if (sortBy == null) {
            sortBy = SortByEnum.startDate;
        }

        Sort sort = sortDir.toString().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy.toString()).ascending() : Sort.by(sortBy.toString()).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<GymnasiumClass> gymnasiumClasses = retrieve(name, startDateStr, endDateStr, pageable);

        //get content for page object
        List<GymnasiumClass> listOfGymnasiumClasses = gymnasiumClasses.getContent();

        List<GymnasiumClassResponseDto> content = listOfGymnasiumClasses.stream().map(gymnasiumClass -> mapToDTO(gymnasiumClass)).collect(Collectors.toList());

        GymnasiumClassPaginated gymnasiumClassResponse = new GymnasiumClassPaginated();
        gymnasiumClassResponse.setContent(content);
        gymnasiumClassResponse.setPageNo(gymnasiumClasses.getNumber());
        gymnasiumClassResponse.setPageSize(gymnasiumClasses.getSize());
        gymnasiumClassResponse.setTotalElements(gymnasiumClasses.getTotalElements());
        gymnasiumClassResponse.setTotalPages(gymnasiumClasses.getTotalPages());

        return gymnasiumClassResponse;
    }

    private Page<GymnasiumClass> retrieve(String name, String startDateStr, String endDateStr, Pageable pageable) {
        Page<GymnasiumClass> gymnasiumClasses = null;
        boolean isNameEmpty = StringUtils.isBlank(name);

        boolean isStartDateEmpty = StringUtils.isBlank(startDateStr);
        boolean isEndDateEmpty = StringUtils.isBlank(endDateStr);
        Date startDate = null;
        Date endDate = null;

        if (!isStartDateEmpty) {
            startDate = Utils.getDateFromString(startDateStr);
        }

        if (!isEndDateEmpty) {
            endDate = Utils.getDateFromString(endDateStr);
        } else {
            if (!isStartDateEmpty) {
                endDate = startDate;
            }
        }

        if (isNameEmpty && isStartDateEmpty && isEndDateEmpty) {
            gymnasiumClasses = gymnasiumClassRepository.findAll(pageable);
        } else if (!isNameEmpty && isStartDateEmpty) {
            gymnasiumClasses = gymnasiumClassRepository.findAllByNameContaining(name, pageable);
        } else if (isNameEmpty && !isStartDateEmpty) {
            gymnasiumClasses = gymnasiumClassRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate, pageable);
        } else {
            gymnasiumClasses = gymnasiumClassRepository.findAllByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(name, startDate, endDate, pageable);
        }
        return gymnasiumClasses;
    }

    @Override
    public List<GymnasiumClassResponseDto> createClasses(GymnasiumClassDto gymnasiumClassDto) {
        List<GymnasiumClassResponseDto> listOfGymnasiumClasses = new ArrayList<>();
        if (basicGymnasiumClassValidations(gymnasiumClassDto)) {
            if (Utils.isDateFormatInvalid(gymnasiumClassDto.getStartDate(), gymnasiumClassDto.getEndDate())) {
                throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
            }
            if (gymnasiumClassDto.getCapacity() <= 0) {
                throw new ForbiddenException("Minimum capacity is 1");
            }

            Date startDate = Utils.getDateFromString(gymnasiumClassDto.getStartDate());
            Date endDate = Utils.getDateFromString(gymnasiumClassDto.getEndDate());

            if (isStartDateBeforeToday(startDate)) {
                throw new ForbiddenException("The start date can't be before today's date");
            }

            if (isEndDateBeforeStartDate(startDate, endDate)) {
                throw new ForbiddenException("The end date can't be before start date");
            }

            if (classAlreadyExistsInThisPeriodOfDates(startDate, endDate)) {
                throw new ForbiddenException("There is already a class in the period of dates provided");
            }

            int getNumberOfDaysBetweenTwoDates = Utils.getNumberOfDaysBetweenTwoDates(startDate, endDate);

            for (int i = 0; i <= getNumberOfDaysBetweenTwoDates; i++) {
                GymnasiumClassResponseDto localGymnasiumClassResponseDto = new GymnasiumClassResponseDto();
                localGymnasiumClassResponseDto.setName(gymnasiumClassDto.getName());
                String finalStartDate = Utils.getStringFromDate(Utils.addNumberOfDaysToDate(startDate, i));

                localGymnasiumClassResponseDto.setDate(finalStartDate);
                localGymnasiumClassResponseDto.setCapacity(gymnasiumClassDto.getCapacity());

                //convert DTO to Entity
                GymnasiumClass gymnasiumClass = mapToEntity(localGymnasiumClassResponseDto);
                GymnasiumClass newGymnasiumClass = gymnasiumClassRepository.save(gymnasiumClass);

                //convert Entity to DTO
                GymnasiumClassResponseDto gymnasiumClassResponse = mapToDTO(newGymnasiumClass);
                listOfGymnasiumClasses.add(gymnasiumClassResponse);
            }
        }
        return listOfGymnasiumClasses;
    }


    @Override
    public GymnasiumClassResponseDto updateClass(GymnasiumClassDto gymnasiumClassDto) {
        GymnasiumClassResponseDto gymnasiumClassResponseDto = null;
        gymnasiumClassDto.setEndDate(gymnasiumClassDto.getStartDate());
        if (basicGymnasiumClassValidations(gymnasiumClassDto)) {
            if (Utils.isDateFormatInvalid(gymnasiumClassDto.getStartDate(), gymnasiumClassDto.getEndDate())) {
                throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
            }
            if (gymnasiumClassDto.getCapacity() <= 0) {
                throw new ForbiddenException("Minimum capacity is 1");
            }

            Date startDate = Utils.getDateFromString(gymnasiumClassDto.getStartDate());

            if (isStartDateBeforeToday(startDate)) {
                throw new ForbiddenException("The start date can't be before today's date");
            }

            Optional<GymnasiumClass> gymnasiumClass = gymnasiumClassRepository.findByStartDate(startDate);

            if (!gymnasiumClass.isPresent()) {
                throw new ResourceNotFoundException("There is no class on the given date");
            }

            GymnasiumClass gymnasiumClassToUpdate = gymnasiumClass.get();
            gymnasiumClassToUpdate.setName(gymnasiumClassDto.getName());
            gymnasiumClassToUpdate.setStartDate(Utils.getDateFromString(gymnasiumClassDto.getStartDate()));
            gymnasiumClassToUpdate.setEndDate(Utils.getDateFromString(gymnasiumClassDto.getEndDate()));
            gymnasiumClassToUpdate.setCapacity(gymnasiumClassDto.getCapacity());

            GymnasiumClass updatedGymnasiumClass = gymnasiumClassRepository.save(gymnasiumClassToUpdate);

            gymnasiumClassResponseDto = mapToDTO(updatedGymnasiumClass);

        }
        return gymnasiumClassResponseDto;
    }

    @Override
    public void deleteClasses(String startDateStr, String endDateStr) {
        if (StringUtils.isBlank(startDateStr)) {
            throw new BadRequestException("Missing input field: startDate");
        }
        if (Utils.isDateFormatInvalid(startDateStr, endDateStr)) {
            throw new BadRequestException("Invalid date format: Valid date format is dd-MM-yyyy");
        }
        boolean isEndDateEmpty = StringUtils.isBlank(endDateStr);
        Date endDate = null;
        Date startDate = Utils.getDateFromString(startDateStr);

        if (!isEndDateEmpty) {
            endDate = Utils.getDateFromString(endDateStr);
            if (isEndDateBeforeStartDate(startDate, endDate)) {
                throw new ForbiddenException("The end date can't be before start date");
            }
            if (!classAlreadyExistsInThisPeriodOfDates(startDate, endDate)) {
                throw new ResourceNotFoundException("There is no class in the period of dates provided");
            }
        }

        int getNumberOfDaysBetweenTwoDates = !isEndDateEmpty ? Utils.getNumberOfDaysBetweenTwoDates(startDate, endDate) : 0;

        if (!isEndDateEmpty) {
            if (getNumberOfDaysBetweenTwoDates >= numberOfExistentClassesOnthisPeriodOfDates(startDate, endDate)) {
                throw new ResourceNotFoundException("In this period of time there are days with no classes, hence it is impossible to make a deletion");
            }
        }

        for (int i = 0; i <= getNumberOfDaysBetweenTwoDates; i++) {
            Date finalStartDate = Utils.addNumberOfDaysToDate(startDate, i);
            GymnasiumClass gymnasiumClass = gymnasiumClassRepository.findByStartDate(finalStartDate).orElseThrow(() -> new ResourceNotFoundException("There is no class on the given date"));
            gymnasiumClassRepository.delete(gymnasiumClass);
        }
    }

    private GymnasiumClassResponseDto mapToDTO(GymnasiumClass gymnasiumClass) {
        GymnasiumClassResponseDto gymnasiumClassResponseDto = new GymnasiumClassResponseDto();
        gymnasiumClassResponseDto.setName(gymnasiumClass.getName());
        gymnasiumClassResponseDto.setDate(gymnasiumClass.getStartDate() != null ? Utils.getStringFromDate(gymnasiumClass.getStartDate()) : null);
        gymnasiumClassResponseDto.setCapacity(gymnasiumClass.getCapacity());
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : gymnasiumClass.getBookings()) {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setMemberName(booking.getMemberName());
            bookingDto.setDate(Utils.getStringFromDate(booking.getDate()));
            bookingDtos.add(bookingDto);
        }
        gymnasiumClassResponseDto.setBookings(bookingDtos);
        return gymnasiumClassResponseDto;
    }

    private GymnasiumClass mapToEntity(GymnasiumClassResponseDto gymnasiumClassResponseDto) {
        GymnasiumClass gymnasiumClass = new GymnasiumClass();
        gymnasiumClass.setName(gymnasiumClassResponseDto.getName());
        gymnasiumClass.setStartDate(gymnasiumClassResponseDto.getDate() != null ? Utils.getDateFromString(gymnasiumClassResponseDto.getDate()) : null);
        gymnasiumClass.setEndDate(gymnasiumClassResponseDto.getDate() != null ? Utils.getDateFromString(gymnasiumClassResponseDto.getDate()) : null);
        gymnasiumClass.setCapacity(gymnasiumClassResponseDto.getCapacity());
        return gymnasiumClass;
    }


    private boolean basicGymnasiumClassValidations(GymnasiumClassDto gymnasiumClassDto) {
        String errorValue = null;
        boolean isValid = true;
        if (StringUtils.isBlank(gymnasiumClassDto.getName())) {
            errorValue = "name";
            isValid = false;
        } else if (StringUtils.isBlank(gymnasiumClassDto.getStartDate())) {
            errorValue = "startDate";
            isValid = false;
        } else if (StringUtils.isBlank(gymnasiumClassDto.getEndDate())) {
            errorValue = "endDate";
            isValid = false;
        } else if (gymnasiumClassDto.getCapacity() == null) {
            errorValue = "capacity";
            isValid = false;
        }
        if (!isValid) {
            throw new BadRequestException("Missing input field: " + errorValue);
        }
        return isValid;
    }

    private boolean classAlreadyExistsInThisPeriodOfDates(Date startDate, Date endDate) {
        return !gymnasiumClassRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate).isEmpty();
    }

    private int numberOfExistentClassesOnthisPeriodOfDates(Date startDate, Date endDate) {
        return gymnasiumClassRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate).size();
    }

    private boolean isStartDateBeforeToday(Date startDate) {
        Instant currentDateInstant = Instant.now();
        Instant startDateInstant = startDate.toInstant();

        LocalDate currentLocalDate = currentDateInstant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startDateLocalDate = startDateInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        return startDateLocalDate.isBefore(currentLocalDate);
    }

    private boolean isEndDateBeforeStartDate(Date startDate, Date endDate) {
        return endDate.before(startDate);
    }
}
