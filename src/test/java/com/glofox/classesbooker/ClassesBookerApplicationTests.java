package com.glofox.classesbooker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.exception.BadRequestException;
import com.glofox.classesbooker.exception.ForbiddenException;
import com.glofox.classesbooker.exception.ResourceNotFoundException;
import com.glofox.classesbooker.payload.BookingDto;
import com.glofox.classesbooker.payload.GymnasiumClassDto;
import com.glofox.classesbooker.utils.Utils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassesBookerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MockMvc mockMvc;

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void testAddGymnasiumClassWithSuccess() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(1L, "Pilates", Utils.getStringFromDate(new Date()), Utils.getStringFromDate(new Date()), 20);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name", Matchers.is(gymnasiumClassDto.getName())))
                .andExpect(jsonPath("$[0].date", Matchers.is(gymnasiumClassDto.getStartDate())))
                .andExpect(jsonPath("$[0].capacity", Matchers.is(gymnasiumClassDto.getCapacity())));

    }

    @Test
    @Order(2)
    public void testAddSecondGymnasiumClassWithSuccess() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(2L, "Aerobics", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)), Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)), 10);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name", Matchers.is(gymnasiumClassDto.getName())))
                .andExpect(jsonPath("$[0].date", Matchers.is(gymnasiumClassDto.getStartDate())))
                .andExpect(jsonPath("$[0].capacity", Matchers.is(gymnasiumClassDto.getCapacity())));

    }

    @Test
    @Order(3)
    public void getListOfAllGymnasiumClasses() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("pageNo", "0");
        paramMap.add("pageSize", "10");
        paramMap.add("sortBy", SortByEnum.startDate.toString());
        paramMap.add("sortDir", SortDirEnum.asc.toString());
        paramMap.add("startDate", Utils.getStringFromDate(new Date()));
        paramMap.add("endDate", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.size()", Matchers.is(2)))
                .andExpect(jsonPath("content[0].name", Matchers.is("Pilates")))
                .andExpect(jsonPath("content[0].date", Matchers.is(Utils.getStringFromDate(new Date()))))
                .andExpect(jsonPath("content[0].capacity", Matchers.is(20)))
                .andExpect(jsonPath("content[1].name", Matchers.is("Aerobics")))
                .andExpect(jsonPath("content[1].date", Matchers.is(Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)))))
                .andExpect(jsonPath("content[1].capacity", Matchers.is(10)));

    }

    @Test
    @Order(4)
    public void getListOfOneGymnasiumClassWhenTwoExist() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("pageNo", "0");
        paramMap.add("pageSize", "10");
        paramMap.add("sortBy", SortByEnum.startDate.toString());
        paramMap.add("sortDir", SortDirEnum.asc.toString());
        paramMap.add("name", "Pilates");
        paramMap.add("startDate", Utils.getStringFromDate(new Date()));
        paramMap.add("endDate", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].name", Matchers.is("Pilates")))
                .andExpect(jsonPath("content[0].date", Matchers.is(Utils.getStringFromDate(new Date()))))
                .andExpect(jsonPath("content[0].capacity", Matchers.is(20)));

    }

    @Test
    @Order(5)
    public void tryToAddGymnasiumClassOnAnAlreadyOccupiedPeriodOfDates() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(2L, "Aerobics", Utils.getStringFromDate(new Date()), Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)), 20);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andExpect(result -> assertEquals("There is already a class in the period of dates provided", result.getResolvedException().getMessage()));
    }

    @Test
    @Order(6)
    public void deleteExistentGymnasiumClass() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("startDate", Utils.getStringFromDate(new Date()));
        paramMap.add("endDate", Utils.getStringFromDate(new Date()));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void tryToDeleteNotExistentGymnasiumClass() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("startDate", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 2)));
        paramMap.add("endDate", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 2)));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no class in the period of dates provided", result.getResolvedException().getMessage()));
    }

    @Test
    @Order(8)
    public void testAddGymnasiumClassWithSuccessForBooking() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(3L, "Pilates", Utils.getStringFromDate(new Date()), Utils.getStringFromDate(new Date()), 20);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name", Matchers.is(gymnasiumClassDto.getName())))
                .andExpect(jsonPath("$[0].date", Matchers.is(gymnasiumClassDto.getStartDate())))
                .andExpect(jsonPath("$[0].capacity", Matchers.is(gymnasiumClassDto.getCapacity())));

    }

    @Test
    @Order(9)
    public void BookSuccessfully() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, "Miguel", Utils.getStringFromDate(new Date()));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberName", Matchers.is(bookingDto.getMemberName())))
                .andExpect(jsonPath("$.date", Matchers.is(bookingDto.getDate())));

    }

    @Test
    @Order(10)
    public void getListOfGymnasiumClassWithBooking() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("pageNo", "0");
        paramMap.add("pageSize", "10");
        paramMap.add("sortBy", SortByEnum.startDate.toString());
        paramMap.add("sortDir", SortDirEnum.asc.toString());
        paramMap.add("name", "Pilates");
        paramMap.add("startDate", Utils.getStringFromDate(new Date()));
        paramMap.add("endDate", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 1)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.size()", Matchers.is(1)))
                .andExpect(jsonPath("content[0].name", Matchers.is("Pilates")))
                .andExpect(jsonPath("content[0].date", Matchers.is(Utils.getStringFromDate(new Date()))))
                .andExpect(jsonPath("content[0].capacity", Matchers.is(19)))
                .andExpect(jsonPath("content[0].bookings.size()", Matchers.is(1)));

    }

    @Test
    @Order(11)
    public void BookUnsuccessfulDueToNotExistentClass() throws Exception {
        BookingDto bookingDto = new BookingDto(2L, "Miguel", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), 2)));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no class on the given date, hence booking is not available", result.getResolvedException().getMessage()));
    }

    @Test
    @Order(12)
    public void BookUnsuccessfulBecauseItAlreadyExists() throws Exception {
        BookingDto bookingDto = new BookingDto(3L, "Miguel", Utils.getStringFromDate(new Date()));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andExpect(result -> assertEquals("Booking already exists", result.getResolvedException().getMessage()));
    }

    @Test
    @Order(13)
    public void deleteBookingSuccessfully() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("memberName", "Miguel");
        paramMap.add("date", Utils.getStringFromDate(new Date()));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/bookings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isOk());
    }

    @Test
    @Order(14)
    public void deleteBookingUnsuccessfulBecauseItDoesNotExist() throws Exception {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("memberName", "John");
        paramMap.add("date", Utils.getStringFromDate(new Date()));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/bookings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params(paramMap))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("No booking found for the given member on the given date", result.getResolvedException().getMessage()));
    }

    @Test
    public void testFailToAddGymnasiumClassDueToErrorOnStartDateFormat() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(1L, "Pilates", "05-022024", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), -1)), 10);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Invalid date format: Valid date format is dd-MM-yyyy", result.getResolvedException().getMessage()));
    }

    @Test
    public void testFailToAddGymnasiumClassDueToStartDateBeforeToday() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(1L, "Pilates", Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), -1)), Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), -1)), 10);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andExpect(result -> assertEquals("The start date can't be before today's date", result.getResolvedException().getMessage()));
    }

    @Test
    public void testFailToAddGymnasiumClassDueToEndDateBeforeStartDate() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(1L, "Pilates", Utils.getStringFromDate(new Date()), Utils.getStringFromDate(Utils.addNumberOfDaysToDate(new Date(), -1)), 10);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andExpect(result -> assertEquals("The end date can't be before start date", result.getResolvedException().getMessage()));
    }

    @Test
    public void testFailToAddGymnasiumClassDueToInvalidCapacity() throws Exception {
        GymnasiumClassDto gymnasiumClassDto = new GymnasiumClassDto(1L, "Pilates", Utils.getStringFromDate(new Date()), Utils.getStringFromDate(new Date()), 0);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/classes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(gymnasiumClassDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andExpect(result -> assertEquals("Minimum capacity is 1", result.getResolvedException().getMessage()));
    }


    /*if (Utils.isDateFormatInvalid(gymnasiumClassDto.getStartDate(), gymnasiumClassDto.getEndDate())) {
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
    }*/
}
