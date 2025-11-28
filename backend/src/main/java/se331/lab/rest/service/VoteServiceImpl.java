package se331.lab.rest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se331.lab.rest.entity.News;
import se331.lab.rest.entity.User;
import se331.lab.rest.entity.Vote;
import se331.lab.rest.repository.NewsRepository;
import se331.lab.rest.repository.UserRepository;
import se331.lab.rest.repository.VoteRepository;
import se331.lab.rest.util.VoteDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

        private final VoteRepository voteRepository;
        private final NewsRepository newsRepository;
        private final UserRepository userRepository;

        @Override
        public VoteDto voteNews(VoteDto voteDto, String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                News news = newsRepository.findById(voteDto.getNewsId())
                                .orElseThrow(() -> new RuntimeException("News not found"));

                Vote vote = Vote.builder()
                                .user(user)
                                .news(news)
                                .isFake(voteDto.isFake())
                                .comment(voteDto.getComment())
                                .image(voteDto.getImage())
                                .build();

                Vote savedVote = voteRepository.save(vote);

                // Update fake status logic here if needed (e.g. recalculate)
                recalculateFakeStatus(news.getId());

                return mapToDto(savedVote);
        }

        @Override
        public List<VoteDto> getVotesByNewsId(Long newsId) {
                return voteRepository.findByNewsId(newsId).stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());
        }

        @Override
        public void deleteVote(Long id) {
                Vote vote = voteRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Vote not found"));
                Long newsId = vote.getNews().getId();
                voteRepository.delete(vote);
                recalculateFakeStatus(newsId);
        }

        private void recalculateFakeStatus(Long newsId) {
                News news = newsRepository.findById(newsId)
                                .orElseThrow(() -> new RuntimeException("News not found"));
                List<Vote> votes = voteRepository.findByNewsId(newsId);

                if (votes.isEmpty()) {
                        news.setFakeStatus("UNKNOWN");
                } else {
                        long fakeCount = votes.stream().filter(Vote::isFake).count();
                        long realCount = votes.size() - fakeCount;
                        if (fakeCount > realCount) {
                                news.setFakeStatus("FAKE");
                        } else if (realCount > fakeCount) {
                                news.setFakeStatus("NOT_FAKE");
                        } else {
                                news.setFakeStatus("UNKNOWN");
                        }
                }
                newsRepository.save(news);
        }

        private VoteDto mapToDto(Vote vote) {
                return VoteDto.builder()
                                .id(vote.getId())
                                .userId(vote.getUser().getId())
                                .username(vote.getUser().getUsername())
                                .newsId(vote.getNews().getId())
                                .isFake(vote.isFake())
                                .comment(vote.getComment())
                                .image(vote.getImage())
                                .build();
        }
}
