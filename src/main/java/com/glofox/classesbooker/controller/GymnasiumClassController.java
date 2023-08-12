package com.glofox.classesbooker.controller;

import com.glofox.classesbooker.enums.SortByEnum;
import com.glofox.classesbooker.enums.SortDirEnum;
import com.glofox.classesbooker.payload.GymnasiumClassDto;
import com.glofox.classesbooker.payload.GymnasiumClassPaginated;
import com.glofox.classesbooker.payload.GymnasiumClassResponseDto;
import com.glofox.classesbooker.service.GymnasiumClassService;
import com.glofox.classesbooker.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@Tag(name = "Gymnasium Classes", description = "Gymnasium Classes Controller Endpoint")
public class GymnasiumClassController {

    @Autowired
    private GymnasiumClassService gymnasiumClassService;

    public GymnasiumClassController(GymnasiumClassService gymnasiumClassService) {
        this.gymnasiumClassService = gymnasiumClassService;
    }

    @Operation(summary = "Retrieve all Classes, paginated, with the optional parameters name, startDate and endDate (date format must be dd-MM-yyyy)")
    @GetMapping()
    public GymnasiumClassPaginated getAllClasses(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy") SortByEnum sortBy,
            @RequestParam(value = "sortDir") SortDirEnum sortDir,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        return gymnasiumClassService.getAllClasses(pageNo, pageSize, sortBy, sortDir, name, startDate, endDate);
    }

    @Operation(summary = "Creates new Classes. The number of classes is based on the interval of days between the starDate and the endDate (date format must be dd-MM-yyyy)")
    @PostMapping()
    public ResponseEntity<List<GymnasiumClassResponseDto>> createClasses(@Valid @RequestBody GymnasiumClassDto gymnasiumClassDto) {
        return new ResponseEntity<>(gymnasiumClassService.createClasses(gymnasiumClassDto), HttpStatus.CREATED);
    }

    /*@Operation(summary = "Updates existent Classes, based on the name")
    @PutMapping()
    public ResponseEntity<List<GymnasiumClassDto>> updateClasses(@Valid @RequestBody GymnasiumClassDto gymnasiumClassDto) {
        return new ResponseEntity<>(gymnasiumClassService.updateClasses(gymnasiumClassDto), HttpStatus.CREATED);
    }*/

    @Operation(summary = "Delete Classes. The number of classes deleted is based on the interval of days between the starDate and the endDate (date format must be dd-MM-yyyy). " +
            "If endDate is not passed only the class with startDate is deleted (if it exists)")
    @DeleteMapping()
    public ResponseEntity<String> deleteClasses(@RequestParam(name = "startDate") String startDate,
                                                @RequestParam(name = "endDate", required = false) String endDate) {
        gymnasiumClassService.deleteClasses(startDate, endDate);
        return new ResponseEntity<>("Classes deleted successfully.", HttpStatus.OK);
    }
}
