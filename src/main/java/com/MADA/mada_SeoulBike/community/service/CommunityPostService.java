package com.MADA.mada_SeoulBike.community.service;

import com.MADA.mada_SeoulBike.community.dto.request.PostCreateRequest;
import com.MADA.mada_SeoulBike.community.dto.request.PostUpdateRequest;
import com.MADA.mada_SeoulBike.community.dto.response.PostResponse;
import com.MADA.mada_SeoulBike.community.entity.CommunityPost;
import com.MADA.mada_SeoulBike.community.repository.CommunityPostRepository;
import com.MADA.mada_SeoulBike.user.entity.User;
import com.MADA.mada_SeoulBike.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    @Transactional
    public PostResponse createPost(PostCreateRequest req, String userEmail) {
        System.out.println("[DEBUG] CommunityPostService.createPost userEmail: " + userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));

        boolean isNotice = "notice".equals(req.getCategory());

        if (isNotice && !"admin".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        CommunityPost post = CommunityPost.builder()
                .category(req.getCategory())
                .title(req.getTitle())
                .content(req.getContent())
                .authorId(String.valueOf(user.getId()))
                .authorName(user.getName())
                .authorRole(user.getRole())
                .createdAt(LocalDateTime.now())
                .likes(0)
                .isNotice(isNotice)
                .build();

        CommunityPost saved = postRepository.save(post);
        return toResponse(saved);
    }

    // 게시글 목록
    public List<PostResponse> listPosts(String category, String author, String keyword) {
        List<CommunityPost> posts;
        if (keyword != null && !keyword.isBlank()) {
            posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
        } else if (category != null) {
            posts = postRepository.findByCategory(category);
        } else if (author != null) {
            // author는 이메일이므로, 이메일로 사용자를 찾아서 ID로 필터링
            User user = userRepository.findByEmail(author)
                    .orElse(null);
            if (user != null) {
                posts = postRepository.findByAuthorId(String.valueOf(user.getId()));
            } else {
                posts = new ArrayList<>();
            }
        } else {
            posts = postRepository.findAll();
        }
        return posts.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 게시글 상세
    public PostResponse getPost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        return toResponse(post);
    }

    // 게시글 수정 (본인/관리자만)
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest req, String userEmail) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));

        boolean isOwner = post.getAuthorId().equals(String.valueOf(user.getId()));
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setCategory(req.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        return toResponse(post);
    }

    // 게시글 삭제 (본인/관리자만)
    @Transactional
    public void deletePost(Long postId, String userEmail) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));

        boolean isOwner = post.getAuthorId().equals(String.valueOf(user.getId()));
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }

    // 좋아요 증가 (추가)
    @Transactional
    public PostResponse likePost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        post.setLikes(post.getLikes() + 1);
        return toResponse(post);
    }

    // 엔티티 → 응답 DTO 변환
    private PostResponse toResponse(CommunityPost post) {
        return PostResponse.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .authorRole(post.getAuthorRole())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likes(post.getLikes())
                .isNotice(post.isNotice())
                .build();
    }
}
