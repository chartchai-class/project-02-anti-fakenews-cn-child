package se331.lab.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se331.lab.rest.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByStatus(String status, Pageable pageable);

    Page<News> findByFakeStatus(String fakeStatus, Pageable pageable);

    Page<News> findByTopicContaining(String topic, Pageable pageable);

    Page<News> findByStatusNot(String status, Pageable pageable);

    Page<News> findByTopicContainingAndStatusNot(String topic, String status, Pageable pageable);
}
