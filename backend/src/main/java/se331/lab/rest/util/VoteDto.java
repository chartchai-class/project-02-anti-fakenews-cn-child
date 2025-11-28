package se331.lab.rest.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private Long id;
    private Long userId;
    private String username; // For display
    private Long newsId;

    @JsonProperty("isFake")
    private boolean isFake;

    private String comment;
    private String image;
}
