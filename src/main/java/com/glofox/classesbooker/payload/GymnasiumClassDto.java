package com.glofox.classesbooker.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GymnasiumClassDto {

    @JsonIgnore
    private long id;

    @JsonProperty("name")
    @NotEmpty
    @Schema(name = "name", type = "string", description = "The name of the class", example = "Aerobics")
    private String name;

    @JsonProperty("startDate")
    @NotEmpty
    @Schema(name = "startDate", type = "string", description = "The start date of the class", example = "12-09-2023")
    private String startDate;

    @JsonProperty("endDate")
    @Schema(name = "endDate", type = "string", description = "The end date of the class", example = "13-09-2023")
    private String endDate;

    @JsonProperty("capacity")
    @NotEmpty
    @Schema(name = "capacity", type = "integer", description = "The maximum capacity the class", example = "10")
    private Integer capacity;
}
