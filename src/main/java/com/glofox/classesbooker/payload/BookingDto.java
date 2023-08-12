package com.glofox.classesbooker.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BookingDto {

    @JsonIgnore
    private long id;

    @JsonProperty("memberName")
    @NotEmpty
    @Schema(name = "memberName", type = "string", description = "The member name", example = "Miguel")
    private String memberName;

    @JsonProperty("date")
    @NotEmpty
    @Schema(name = "date", type = "string", description = "The date of the booking", example = "12-08-2023")
    private String date;
}
