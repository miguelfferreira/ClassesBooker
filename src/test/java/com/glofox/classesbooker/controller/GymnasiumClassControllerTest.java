package com.glofox.classesbooker.controller;


import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.payload.GymnasiumClassPaginated;
import com.glofox.classesbooker.payload.GymnasiumClassResponseDto;
import com.glofox.classesbooker.service.GymnasiumClassService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GymnasiumClassControllerTest.class)
class GymnasiumClassControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GymnasiumClassService gymnasiumClassService;


    @Test
    @DisplayName("Should List All Gymnasium Classes when making GET request to endpoint - /classes/")
    void shouldRetrieveGymnasiumClasses() throws Exception {
        List<GymnasiumClassResponseDto> gymnasiumClassesResponseDto = new ArrayList<>();
        GymnasiumClassResponseDto gymnasiumClassResponseDto1 = new GymnasiumClassResponseDto("Pilates", "12-08-2023", 20, new ArrayList<>());
        GymnasiumClassResponseDto gymnasiumClassResponseDto2 = new GymnasiumClassResponseDto("Pilates", "13-08-2023", 10, new ArrayList<>());
        gymnasiumClassesResponseDto.add(gymnasiumClassResponseDto1);
        gymnasiumClassesResponseDto.add(gymnasiumClassResponseDto2);
        GymnasiumClassPaginated gymnasiumClassPaginated = new GymnasiumClassPaginated();
        gymnasiumClassPaginated.setContent(gymnasiumClassesResponseDto);

        mockMvc.perform(get("http://localhost:8080/classes")
                        .param("pageNo", "0")
                        .param("pageSize", "10")
                        .param("sortBy", SortByEnum.startDate.toString())
                        .param("sortDir", SortDirEnum.asc.toString())
                        .param("name", "Pilates")
                        .param("startDate", "12-08-2023")
                        .param("endDate", "13-08-2023"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()", Matchers.is(2)));

    }
}
