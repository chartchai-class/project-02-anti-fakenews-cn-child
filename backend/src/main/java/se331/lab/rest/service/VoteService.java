package se331.lab.rest.service;

import se331.lab.rest.util.VoteDto;
import java.util.List;

public interface VoteService {
    VoteDto voteNews(VoteDto voteDto, String username);

    List<VoteDto> getVotesByNewsId(Long newsId);

    void deleteVote(Long id);
}
