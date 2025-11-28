package se331.lab.rest.config;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se331.lab.rest.entity.News;
import se331.lab.rest.entity.User;
import se331.lab.rest.entity.Vote;
import se331.lab.rest.repository.NewsRepository;
import se331.lab.rest.repository.UserRepository;
import se331.lab.rest.repository.VoteRepository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

        private final UserRepository userRepository;
        private final NewsRepository newsRepository;
        private final VoteRepository voteRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                Faker faker = new Faker();
                Random random = new Random();

                List<User> users = new ArrayList<>();

                // Ensure Admin exists
                if (userRepository.findByUsername("admin").isEmpty()) {
                        User admin = User.builder()
                                        .username("admin")
                                        .password(passwordEncoder.encode("password"))
                                        .firstname("Admin")
                                        .lastname("User")
                                        .email("admin@example.com")
                                        .role("ROLE_ADMIN")
                                        .build();
                        users.add(userRepository.save(admin));
                        System.out.println("Seeded admin user");
                }

                // Ensure Member exists
                if (userRepository.findByUsername("member").isEmpty()) {
                        User member = User.builder()
                                        .username("member")
                                        .password(passwordEncoder.encode("password"))
                                        .firstname("Member")
                                        .lastname("User")
                                        .email("member@example.com")
                                        .role("ROLE_MEMBER")
                                        .build();
                        users.add(userRepository.save(member));
                        System.out.println("Seeded member user");
                }

                // Seed random users if low count
                if (userRepository.count() < 5) {
                        for (int i = 0; i < 20; i++) {
                                User user = User.builder()
                                                .username(faker.name().username() + i)
                                                .password(passwordEncoder.encode("password"))
                                                .firstname(faker.name().firstName())
                                                .lastname(faker.name().lastName())
                                                .email(i + faker.internet().emailAddress())
                                                .role("ROLE_READER")
                                                .build();
                                users.add(userRepository.save(user));
                        }
                        System.out.println("Seeded random users");
                }

                // Reload all users for voting
                users = userRepository.findAll();

                // Seed News if empty
                if (newsRepository.count() == 0) {
                        for (int i = 0; i < 100; i++) {
                                StringBuilder contentBuilder = new StringBuilder();
                                while (contentBuilder.length() < 6000) {
                                        contentBuilder.append(faker.company().catchPhrase()).append(". ");
                                        contentBuilder.append(faker.company().bs()).append(". ");
                                        contentBuilder.append("The ").append(faker.company().industry())
                                                        .append(" industry is growing. ");
                                        contentBuilder.append(faker.lorem().sentence()).append(" ");
                                        contentBuilder.append("\n\n");
                                }

                                contentBuilder.append("\n\n--- Interview Details ---\n");
                                contentBuilder.append("Interviewer: ").append(faker.name().fullName()).append("\n");
                                contentBuilder.append("Interviewee: ").append(faker.name().fullName()).append("\n");
                                contentBuilder.append("Time: ").append(faker.date().past(30, TimeUnit.DAYS).toString())
                                                .append("\n");
                                contentBuilder.append("Location: ").append(faker.address().city()).append("\n");

                                String status = "PUBLISHED";
                                String fakeStatus = random.nextBoolean() ? "FAKE" : "NOT_FAKE";
                                if (random.nextInt(10) < 2)
                                        fakeStatus = "UNKNOWN";

                                News news = News.builder()
                                                .topic(faker.company().catchPhrase())
                                                .content(contentBuilder.toString())
                                                .status(status)
                                                .fakeStatus(fakeStatus)
                                                .reporter(users.get(0)) // Assign to first user (admin/member)
                                                .createdTime(faker.date().past(30, TimeUnit.DAYS).toInstant()
                                                                .atZone(ZoneId.systemDefault())
                                                                .toLocalDateTime())
                                                .image("https://picsum.photos/seed/" + random.nextInt(1000)
                                                                + "/800/600")
                                                .build();

                                news = newsRepository.save(news);

                                // Create 50 Comments (Votes) per News
                                List<Vote> votes = new ArrayList<>();

                                // Generate balanced votes (20-30 fake out of 50)
                                int targetFake = 20 + random.nextInt(11);
                                List<Boolean> voteDecisions = new ArrayList<>();
                                for (int k = 0; k < targetFake; k++)
                                        voteDecisions.add(true);
                                for (int k = 0; k < (50 - targetFake); k++)
                                        voteDecisions.add(false);
                                java.util.Collections.shuffle(voteDecisions);

                                for (int j = 0; j < 50; j++) {
                                        User voter = users.get(random.nextInt(users.size()));
                                        boolean isFake = voteDecisions.get(j);

                                        Vote vote = Vote.builder()
                                                        .user(voter)
                                                        .news(news)
                                                        .isFake(isFake)
                                                        .comment("I think this is " + (isFake ? "FAKE" : "REAL")
                                                                        + " because " + faker.company().bs())
                                                        .image(random.nextBoolean()
                                                                        ? "https://picsum.photos/seed/"
                                                                                        + random.nextInt(1000)
                                                                                        + "/200/200"
                                                                        : null)
                                                        .build();
                                        votes.add(vote);
                                }
                                voteRepository.saveAll(votes);
                                System.out.println("Seeded News " + (i + 1));
                        }
                } else {
                        System.out.println("News already seeded, skipping...");
                }
        }
}
