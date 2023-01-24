package com.example.blogservice.audit;

import com.example.blogservice.dto.PostDtoRequest;
import com.example.blogservice.initializer.DatabaseContainerInitializer;
import com.example.blogservice.model.ModelUpdateStatistics;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.ModelUpdateStatisticsRepository;
import com.example.blogservice.repository.PostRepository;
import com.example.blogservice.repository.UserRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.ModelUpdateStatisticsService;
import com.example.blogservice.service.PostService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class AsyncAuditTest extends DatabaseContainerInitializer {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PostService postService;

    @SpyBean
    private ModelUpdateStatisticsRepository modelUpdateStatisticsRepository;

    private PostDtoRequest post;
    @SpyBean
    private PostRepository postRepository;
    @SpyBean
    private UserRepository userRepository;
    private User user;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    public void setUp(){
        user=User.builder()
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(13))
                .email("myachinenergo@mail.ru")
                .username("artemka")
                .password("somePassword")
                .role(Role.ROLE_ADMIN)
                .build();

        authenticatedUser = new AuthenticatedUser("artemka",
                "someToken", Role.ROLE_ADMIN.name());
    }

    @Test
    @SneakyThrows
    void checkCount(){
        userRepository.save(user);
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch  = new CountDownLatch(numberOfThreads);

        for (int i=0; i<numberOfThreads;i++){
            executorService.execute(()->{
                postService.save(PostDtoRequest.builder()
                                .title("DAsd")
                                .content("DSAd")
                                .userId(user.getId())
                        .build(),authenticatedUser);
                latch.countDown();
            });
        }
        latch.await();

        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) applicationContext.getBean("threadPoolTaskExecutor");
        while (executor.getThreadPoolExecutor().getCompletedTaskCount()<numberOfThreads){
            Thread.yield();
        }
        executorService.shutdown();
        assertEquals(numberOfThreads,postRepository.count());
        assertThat(modelUpdateStatisticsRepository.findAll())
                .hasSize(numberOfThreads);
    }
}
