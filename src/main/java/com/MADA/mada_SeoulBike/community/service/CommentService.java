package com.MADA.mada_SeoulBike.community.service;

import com.MADA.mada_SeoulBike.community.dto.request.CreateCommentRequest;
import com.MADA.mada_SeoulBike.community.dto.request.UpdateCommentRequest;
import com.MADA.mada_SeoulBike.community.dto.response.CommentResponse;
import com.MADA.mada_SeoulBike.community.entity.Comment;
import com.MADA.mada_SeoulBike.community.entity.CommunityPost;
import com.MADA.mada_SeoulBike.community.repository.CommentRepository;
import com.MADA.mada_SeoulBike.community.repository.CommunityPostRepository;
import com.MADA.mada_SeoulBike.user.entity.User;
import com.MADA.mada_SeoulBike.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    public CommentResponse createComment(Long postId, CreateCommentRequest req, String userEmail) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .content(req.getContent())
                .author(user.getName())
                .authorId(String.valueOf(user.getId()))
                .likes(0)
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        return toResponse(saved);
    }

    // 댓글 리스트 (게시글별)
    public List<CommentResponse> getComments(Long postId) {
        List<Comment> list = commentRepository.findByPostId(postId);
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long postId, Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        // postId 일치 여부 체크(프론트 방어용)
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("게시글 정보가 일치하지 않습니다.");
        }
        // 권한 체크
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
        boolean isOwner = comment.getAuthorId().equals(String.valueOf(user.getId()));
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long postId, Long commentId, UpdateCommentRequest req, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        // postId 일치 여부 체크
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("게시글 정보가 일치하지 않습니다.");
        }
        // 권한 체크
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
        boolean isOwner = comment.getAuthorId().equals(String.valueOf(user.getId()));
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        comment.setContent(req.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updated = commentRepository.save(comment);
        return toResponse(updated);
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .authorId(comment.getAuthorId())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
