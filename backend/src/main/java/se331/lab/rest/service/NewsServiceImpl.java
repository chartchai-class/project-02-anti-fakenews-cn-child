package se331.lab.rest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se331.lab.rest.entity.News;
import se331.lab.rest.entity.User;
import se331.lab.rest.repository.NewsRepository;
import se331.lab.rest.repository.UserRepository;
import se331.lab.rest.util.NewsDto;
import se331.lab.rest.util.VoteDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    public Page<NewsDto> getAllNews(Pageable pageable, boolean isAdmin) {
        // By default, do not show HIDDEN news even for admin, unless explicitly
        // requested via other methods
        return newsRepository.findByStatusNot("HIDDEN", pageable).map(this::mapToDto);
    }

    @Override
    public Page<NewsDto> getNewsByStatus(String status, Pageable pageable) {
        return newsRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    @Override
    public Page<NewsDto> getNewsByFakeStatus(String fakeStatus, Pageable pageable) {
        return newsRepository.findByFakeStatus(fakeStatus, pageable).map(this::mapToDto);
    }

    @Override
    public Page<NewsDto> searchNews(String keyword, Pageable pageable, boolean isAdmin) {
        return newsRepository.findByTopicContainingAndStatusNot(keyword, "HIDDEN", pageable).map(this::mapToDto);
    }

    @Override
    public NewsDto getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        return mapToDto(news);
    }

    @Override
    public NewsDto createNews(NewsDto newsDto, String username) {
        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        News news = News.builder()
                .topic(newsDto.getTopic())
                .content(newsDto.getContent())
                .status("CREATED") // Default status
                .fakeStatus("UNKNOWN") // Default fake status
                .reporter(reporter)
                .createdTime(LocalDateTime.now())
                .image(newsDto.getImage())
                .build();

        News savedNews = newsRepository.save(news);
        return mapToDto(savedNews);
    }

    @Override
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setStatus("HIDDEN");
        newsRepository.save(news);
    }

    private NewsDto mapToDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .topic(news.getTopic())
                .content(news.getContent())
                .status(news.getStatus())
                .fakeStatus(news.getFakeStatus())
                .reporterName(news.getReporter().getUsername())
                .createdTime(news.getCreatedTime())
                .image(news.getImage())
                .votes(news.getVotes() != null ? news.getVotes().stream().map(vote -> VoteDto.builder()
                        .id(vote.getId())
                        .userId(vote.getUser().getId())
                        .username(vote.getUser().getUsername())
                        .newsId(vote.getNews().getId())
                        .isFake(vote.isFake())
                        .comment(vote.getComment())
                        .image(vote.getImage())
                        .build()).collect(Collectors.toList()) : null)
                .build();
    }
}
