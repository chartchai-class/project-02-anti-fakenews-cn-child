package se331.lab.rest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String content; // Full details

    private String status; // CREATED, PUBLISHED
    private String fakeStatus; // FAKE, NOT_FAKE, UNKNOWN (Calculated based on votes)

    @ManyToOne
    private User reporter;

    private LocalDateTime createdTime;
    private String image;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    private List<Vote> votes;
}
