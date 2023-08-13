package com.glofox.classesbooker.service;

import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.payload.GymnasiumClassDto;
import com.glofox.classesbooker.payload.GymnasiumClassPaginated;
import com.glofox.classesbooker.payload.GymnasiumClassResponseDto;

import java.util.List;

public interface GymnasiumClassService {
    GymnasiumClassPaginated getAllClasses(int pageNo, int pageSize, SortByEnum sortBy, SortDirEnum sortDir, String name, String startDateStr, String endDateStr);

    List<GymnasiumClassResponseDto> createClasses(GymnasiumClassDto gymnasiumClassDto);

    GymnasiumClassResponseDto updateClass(GymnasiumClassDto gymnasiumClassDto);

    void deleteClasses(String startDateStr, String endDateStr);
}
