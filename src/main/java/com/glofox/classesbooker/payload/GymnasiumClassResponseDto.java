package com.glofox.classesbooker.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GymnasiumClassResponseDto {

    @JsonProperty("name")
    @NotEmpty
    @Schema(name = "name", type = "string", description = "The name of the class", example = "Aerobics")
    private String name;

    @JsonProperty("date")
    @NotEmpty
    @Schema(name = "date", type = "string", description = "The start of the class", example = "12-09-2023")
    private String date;

    @JsonProperty("capacity")
    @NotEmpty
    @Schema(name = "capacity", type = "string", description = "The maximum capacity the class", example = "10")
    private int capacity;

    @JsonProperty("bookings")
    @Schema(name = "bookings", type = "list", description = "The bookings of the displayed class")
    private List<BookingDto> bookings;

}
