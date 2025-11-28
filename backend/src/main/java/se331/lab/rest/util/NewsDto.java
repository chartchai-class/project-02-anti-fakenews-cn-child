package se331.lab.rest.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {
    private Long id;
    private String topic;
    private String content;
    private String status;
    private String fakeStatus;
    private String reporterName; // For display
    private LocalDateTime createdTime;
    private String image;
    private List<VoteDto> votes;
}
