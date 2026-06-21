package com.moassam.admin.application.required;

import com.moassam.post.domain.post.*;
import com.moassam.shared.config.JpaAuditingConfig;
import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import com.moassam.user.domain.UserRegisterRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class AdminPostQueryRepositoryTest {

    @Autowired
    private AdminPostQueryRepository adminPostQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAdminPostSummaries_withoutFilters() {
        User user = User.register(new UserRegisterRequest(
                Provider.KAKAO,
                "provider-id",
                "test@example.com",
                "작성자",
                null
        ));
        entityManager.persist(user);
        entityManager.flush();

        Post post = Post.create(
                user.getId(),
                "게시글 제목",
                "본문",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );
        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        var result = adminPostQueryRepository.findAdminPostSummaries(
                null,
                null,
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).postId()).isEqualTo(post.getId());
        assertThat(result.getContent().get(0).title()).isEqualTo("게시글 제목");
        assertThat(result.getContent().get(0).author()).isEqualTo("작성자");
        assertThat(result.getContent().get(0).category()).isEqualTo(Category.FREE);
        assertThat(result.getContent().get(0).viewCount()).isZero();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAdminPostSummaries_filterByCategory() {
        User user = User.register(new UserRegisterRequest(
                Provider.KAKAO,
                "provider-id",
                "test@example.com",
                "작성자",
                null
        ));
        entityManager.persist(user);
        entityManager.flush();

        Post freePost = Post.create(
                user.getId(),
                "자유 게시글",
                "본문",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        Post moabangPost = Post.create(
                user.getId(),
                "모아방 게시글",
                "본문",
                Category.MOABANG,
                PostAge.AGE_3,
                ResourceType.ACTIVITY,
                null
        );

        entityManager.persist(freePost);
        entityManager.persist(moabangPost);
        entityManager.flush();
        entityManager.clear();

        var result = adminPostQueryRepository.findAdminPostSummaries(
                Category.FREE,
                null,
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("자유 게시글");
        assertThat(result.getContent().get(0).category()).isEqualTo(Category.FREE);
    }

    @Test
    void findAdminPostSummaries_filterByTitleKeyword() {
        User user = User.register(new UserRegisterRequest(
                Provider.KAKAO,
                "provider-id",
                "test@example.com",
                "작성자",
                null
        ));
        entityManager.persist(user);
        entityManager.flush();

        Post matchedPost = Post.create(
                user.getId(),
                "검색되는 게시글",
                "본문",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        Post unmatchedPost = Post.create(
                user.getId(),
                "다른 게시글",
                "본문",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        entityManager.persist(matchedPost);
        entityManager.persist(unmatchedPost);
        entityManager.flush();
        entityManager.clear();

        var result = adminPostQueryRepository.findAdminPostSummaries(
                null,
                "검색",
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("검색되는 게시글");
    }

    @Test
    void findAdminPostSummaries_filterByAuthorKeyword() {
        User user = User.register(new UserRegisterRequest(
                Provider.KAKAO,
                "provider-id",
                "test@example.com",
                "작성자검색",
                null
        ));
        entityManager.persist(user);
        entityManager.flush();

        Post post = Post.create(
                user.getId(),
                "게시글 제목",
                "본문",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        var result = adminPostQueryRepository.findAdminPostSummaries(
                null,
                "검색",
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).author()).isEqualTo("작성자검색");
    }
}
