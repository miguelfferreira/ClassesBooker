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
public class GymnasiumClassPaginated {
    private List<GymnasiumClassResponseDto> content;
    @JsonProperty("pageNo")
    @NotEmpty
    @Schema(name = "pageNo", type = "int", description = "The current page", example = "1")
    private int pageNo;

    @JsonProperty("pageSize")
    @NotEmpty
    @Schema(name = "pageSize", type = "int", description = "The maximum size of a page", example = "10")
    private int pageSize;

    @JsonProperty("totalElements")
    @NotEmpty
    @Schema(name = "totalElements", type = "long", description = "The total number of Elements (the total number of classes fetched)", example = "1")
    private long totalElements;

    @JsonProperty("totalPages")
    @NotEmpty
    @Schema(name = "totalPages", type = "string", description = "The total number of pages", example = "1")
    private int totalPages;
}
