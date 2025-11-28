package se331.lab.rest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import se331.lab.rest.util.NewsDto;

public interface NewsService {
    Page<NewsDto> getAllNews(Pageable pageable, boolean isAdmin);

    Page<NewsDto> getNewsByStatus(String status, Pageable pageable);

    Page<NewsDto> getNewsByFakeStatus(String fakeStatus, Pageable pageable);

    Page<NewsDto> searchNews(String keyword, Pageable pageable, boolean isAdmin);

    NewsDto getNewsById(Long id);

    NewsDto createNews(NewsDto newsDto, String username);

    void deleteNews(Long id);
}
