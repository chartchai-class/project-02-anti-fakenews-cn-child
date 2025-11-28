package se331.lab.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.lab.rest.service.NewsService;
import se331.lab.rest.util.NewsDto;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Page<NewsDto>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fakeStatus,
            @RequestParam(required = false) String keyword,
            Authentication authentication) {

        PageRequest pageable = PageRequest.of(page, size);
        boolean isAdmin = false;
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            isAdmin = true;
        }

        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(newsService.searchNews(keyword, pageable, isAdmin));
        } else if (status != null) {
            return ResponseEntity.ok(newsService.getNewsByStatus(status, pageable));
        } else if (fakeStatus != null) {
            return ResponseEntity.ok(newsService.getNewsByFakeStatus(fakeStatus, pageable));
        } else {
            return ResponseEntity.ok(newsService.getAllNews(pageable, isAdmin));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<NewsDto> createNews(@RequestBody NewsDto newsDto, Authentication authentication) {
        return ResponseEntity.ok(newsService.createNews(newsDto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok().build();
    }
}
