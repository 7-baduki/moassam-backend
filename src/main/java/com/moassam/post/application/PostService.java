package com.moassam.post.application;

import com.moassam.credit.application.provided.CreditCharger;
import com.moassam.post.application.provided.post.PostDeleter;
import com.moassam.post.application.provided.post.PostFinder;
import com.moassam.post.application.provided.post.PostUpdater;
import com.moassam.post.application.required.PostFileRepository;
import com.moassam.post.application.required.PostLikeRepository;
import com.moassam.post.domain.post.*;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.adapter.filestorage.FileStorage;
import com.moassam.post.application.provided.post.PostCreator;
import com.moassam.post.application.required.PostRepository;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService implements PostCreator, PostFinder, PostUpdater, PostDeleter {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileStorage fileStorage;
    private final CreditCharger creditCharger;

    @Override
    @Transactional
    public Long createPost(
            Long userId,
            PostCreateRequest request,
            List<MultipartFile> files,
            List<MultipartFile> editorImages
    ) {
        validateTotalUploadSize(files, editorImages);
        validateEditorImages(editorImages);

        Post post = Post.create(
                userId,
                request.title(),
                request.content(),
                request.category(),
                request.postAge(),
                request.resourceType(),
                request.headTag()
        );

        Post savedPost = postRepository.save(post);

        creditCharge(userId, savedPost);

        List<PostFile> uploadedFiles = new ArrayList<>();
        uploadedFiles.addAll(upload(savedPost.getId(), files, FileType.FILE));
        uploadedFiles.addAll(upload(savedPost.getId(), editorImages, FileType.IMAGE));

        postFileRepository.saveAll(uploadedFiles);

        return savedPost.getId();
    }

    @Override
    public PostDetail getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        User author = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        List<PostFile> files = postFileRepository.findAllByPostId(post.getId());

        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        //TODO: 북마크 기능 개발 후 적용 필요
        return new PostDetail(post, author.getNickname(), files, isLiked, false);
    }

    @Override
    @Transactional
    public Long updatePost(Long userId, Long postId, PostUpdateRequest request, List<MultipartFile> files, List<MultipartFile> editorImages) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(PostErrorCode.POST_FORBIDDEN);
        }

        validateTotalUploadSize(files, editorImages);
        validateEditorImages(editorImages);

        List<PostFile> deleteTargets = findDeleteTargets(postId, request.deleteFileIds());

        post.update(
                request.title(),
                request.content(),
                request.category(),
                request.postAge(),
                request.resourceType(),
                request.headTag()
        );

        deletePostFiles(deleteTargets);

        List<PostFile> uploadedFiles = new ArrayList<>();
        uploadedFiles.addAll(upload(postId, files, FileType.FILE));
        uploadedFiles.addAll(upload(postId, editorImages, FileType.IMAGE));

        postFileRepository.saveAll(uploadedFiles);

        return post.getId();
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(PostErrorCode.POST_FORBIDDEN);
        }

        List<PostFile> postFiles = postFileRepository.findAllByPostId(postId);
        postFiles.forEach(file -> fileStorage.delete(file.getUrl()));
        postFileRepository.deleteAll(postFiles);

        postRepository.delete(post);
    }

    private List<PostFile> findDeleteTargets(Long postId, List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return List.of();
        }

        List<PostFile> deleteTargets = postFileRepository.findAllByIdInAndPostId(fileIds, postId);

        if (deleteTargets.size() != fileIds.size()) {
            throw new BusinessException(PostErrorCode.POST_FILE_NOT_FOUND);
        }

        return deleteTargets;
    }

    private void deletePostFiles(List<PostFile> deleteTargets) {
        if (deleteTargets.isEmpty()) {
            return;
        }

        deleteTargets.forEach(file -> fileStorage.delete(file.getUrl()));
        postFileRepository.deleteAll(deleteTargets);
    }

    private List<PostFile> upload(Long postId, List<MultipartFile> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    String url = fileStorage.upload(file, "posts");
                    return PostFile.create(
                            postId,
                            file.getOriginalFilename(),
                            url,
                            file.getSize(),
                            fileType
                    );
                })
                .toList();
    }

    private void validateTotalUploadSize(
            List<MultipartFile> files,
            List<MultipartFile> editorImages
    ) {
        long totalSize = getTotalSize(files) + getTotalSize(editorImages);

        if (totalSize > 10 * 1024 * 1024) {
            throw new BusinessException(PostErrorCode.POST_UPLOAD_SIZE_EXCEEDED);
        }
    }

    private long getTotalSize(List<MultipartFile> files) {
        if (files == null) {
            return 0;
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .mapToLong(MultipartFile::getSize)
                .sum();
    }


    private void validateEditorImages(List<MultipartFile> editorImages) {
        if (editorImages == null) {
            return;
        }

        for (MultipartFile file : editorImages) {
            if (!isImage(file.getOriginalFilename())) {
                throw new BusinessException(PostErrorCode.POST_INVALID_EDITOR_IMAGE);
            }
        }
    }

    private boolean isImage(String filename) {
        if(filename == null) {
            return false;
        }

        String lowerName = filename.toLowerCase();

        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
    }

    private void creditCharge(Long userId, Post post) {
        switch (post.getCategory()) {
            case FREE -> creditCharger.chargeForFreePost(userId, post.getId());
            case MOABANG -> creditCharger.chargeForMoabangPost(userId, post.getId());
        }
    }
}
