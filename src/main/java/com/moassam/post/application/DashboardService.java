package com.moassam.post.application;

import com.moassam.post.application.provided.dashboard.DashboardFinder;
import com.moassam.post.application.required.PostFileRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.*;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DashboardService implements DashboardFinder {

    private final static String DEFAULT_THUMBNAIL_URL = "https://kr.object.ncloudstorage.com/moassam-storage/posts/dashboard/moabang_default.png";

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final UserRepository userRepository;

    @Override
    public Page<MoabangDashboardDetail> getMoabangDashboard(Long userId, PostAge postAge, ResourceType resourceType, int page, int size) {
        Pageable pageable = createPageable(page, size);

        Page<Post> moabangPosts = postRepository.findMoabangDashboard(
                Category.MOABANG,
                postAge,
                resourceType,
                pageable
        );

        Map<Long, String> authorNicknames = getAuthorNicknames(moabangPosts.getContent());

        Map<Long, String> thumbnailUrls = getThumbnailUrls(moabangPosts.getContent());

        return moabangPosts.map(post -> new MoabangDashboardDetail(
                post.getId(),
                post.getTitle(),
                authorNicknames.get(post.getUserId()),
                thumbnailUrls.getOrDefault(post.getId(), DEFAULT_THUMBNAIL_URL),
                post.getPostAge(),
                post.getResourceType(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        ));
    }

    @Override
    public Page<FreeDashboardDetail> getFreeDashboard(Long userId, HeadTag headTag, int page, int size) {
        Pageable pageable = createPageable(page, size);

        Page<Post> freePosts = postRepository.findFreeDashboard(
                Category.FREE,
                headTag,
                pageable
        );

        Map<Long, String> authorNicknames = getAuthorNicknames(freePosts.getContent());

        return freePosts.map(post -> new FreeDashboardDetail(
                post.getId(),
                post.getTitle(),
                authorNicknames.get(post.getUserId()),
                createContentPreview(post.getContent()),
                post.getHeadTag(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        ));
    }

    @Override
    public Page<MoabangDashboardDetail> searchMoabang(Long userId, String keyword, int page, int size) {
        Pageable pageable = createPageable(page, size);

        Page<Post> posts = postRepository.findAllByCategoryAndTitleContainingIgnoreCase(
                Category.MOABANG, keyword, pageable
        );

        Map<Long, String> authorNicknames = getAuthorNicknames(posts.getContent());

        Map<Long, String> thumbnailUrls = getThumbnailUrls(posts.getContent());

        return posts.map(post -> new MoabangDashboardDetail(
                post.getId(),
                post.getTitle(),
                authorNicknames.get(post.getUserId()),
                thumbnailUrls.getOrDefault(post.getId(), DEFAULT_THUMBNAIL_URL),
                post.getPostAge(),
                post.getResourceType(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        ));
    }

    @Override
    public Page<FreeDashboardDetail> searchFree(Long userId, String keyword, int page, int size) {
        Pageable pageable = createPageable(page, size);

        Page<Post> posts = postRepository.findAllByCategoryAndTitleContainingIgnoreCase(
                Category.FREE, keyword, pageable
        );

        Map<Long, String> authorNicknames = getAuthorNicknames(posts.getContent());

        return posts.map(post -> new FreeDashboardDetail(
                post.getId(),
                post.getTitle(),
                authorNicknames.get(post.getUserId()),
                createContentPreview(post.getContent()),
                post.getHeadTag(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        ));
    }

    private Map<Long, String> getThumbnailUrls(List<Post> posts) {
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        if(postIds.isEmpty()) {
            return Map.of();
        }

        return postFileRepository.findAllByPostIdIn(postIds).stream()
                .filter(this::isThumbnailCandidate)
                .collect(Collectors.groupingBy(
                        PostFile::getPostId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                files -> files.stream()
                                        .sorted(Comparator.comparing(file -> file.getFileType() != FileType.IMAGE))
                                        .findFirst()
                                        .map(PostFile::getUrl)
                                        .orElse(DEFAULT_THUMBNAIL_URL)
                        )
                ));
    }

    private boolean isThumbnailCandidate(PostFile file) {
        if (file.getFileType() == FileType.IMAGE) {
            return true;
        }

        String originalName = file.getOriginalName();
        if (originalName == null) {
            return false;
        }

        String lowerName = originalName.toLowerCase();

        return lowerName.endsWith(".png")
                || lowerName.endsWith(".jpg")
                || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".webp")
                || lowerName.endsWith(".gif");
    }

    private Pageable createPageable(int page, int size) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.clamp(size, 1, 50);

        return PageRequest.of(pageIndex, pageSize);
    }

    private Map<Long, String> getAuthorNicknames(List<Post> posts) {
        List<Long> userIdList = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .toList();

        return userRepository.findAllByIdIn(userIdList).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user.isDeleted() ? "탈퇴한 사용자" : user.getNickname()
                ));
    }

    private String createContentPreview(String content) {
        if (content == null) {
            return "";
        }

        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

}
