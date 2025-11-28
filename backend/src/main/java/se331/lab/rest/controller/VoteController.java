package se331.lab.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.lab.rest.service.VoteService;
import se331.lab.rest.util.VoteDto;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER') or hasRole('READER') or hasRole('ADMIN')")
    public ResponseEntity<VoteDto> voteNews(@RequestBody VoteDto voteDto, Authentication authentication) {
        return ResponseEntity.ok(voteService.voteNews(voteDto, authentication.getName()));
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<VoteDto>> getVotesByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(voteService.getVotesByNewsId(newsId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        voteService.deleteVote(id);
        return ResponseEntity.ok().build();
    }
}
