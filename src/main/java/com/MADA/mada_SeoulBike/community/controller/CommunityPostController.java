package com.MADA.mada_SeoulBike.community.controller;

import com.MADA.mada_SeoulBike.community.dto.request.CreateCommentRequest;
import com.MADA.mada_SeoulBike.community.dto.request.PostCreateRequest;
import com.MADA.mada_SeoulBike.community.dto.request.PostUpdateRequest;
import com.MADA.mada_SeoulBike.community.dto.request.UpdateCommentRequest;
import com.MADA.mada_SeoulBike.community.dto.response.CommentResponse;
import com.MADA.mada_SeoulBike.community.dto.response.PostResponse;
import com.MADA.mada_SeoulBike.community.service.CommentService;
import com.MADA.mada_SeoulBike.community.service.CommunityPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService postService;
    private final CommentService commentService;

    // 전체/카테고리/작성자/검색
    @GetMapping
    public List<PostResponse> listPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String keyword
    ) {
        return postService.listPosts(category, author, keyword);
    }

    // 상세
    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 작성
    @PostMapping
    public PostResponse createPost(@RequestBody PostCreateRequest req, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail"); // Jwt필터에서 저장
        System.out.println("[DEBUG] createPost email: " + email + ", req: " + req);
        return postService.createPost(req, email);
    }

    // 수정
    @PutMapping("/{id}")
    public PostResponse updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest req,
            HttpServletRequest request
    ) {
        String email = (String) request.getAttribute("userEmail");
        return postService.updatePost(id, req, email);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        postService.deletePost(id, email);
    }

    // 좋아요 (추가)
    @PostMapping("/{id}/like")
    public PostResponse likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    // 댓글 등록
    @PostMapping("/{id}/comments")
    public CommentResponse createComment(
            @PathVariable Long id,
            @RequestBody CreateCommentRequest request,
            HttpServletRequest servletRequest  // JWT 필터에서 userEmail을 저장
    ) {
        String userEmail = (String) servletRequest.getAttribute("userEmail");
        if (userEmail == null || userEmail.isBlank()) {
            throw new RuntimeException("사용자 인증 정보가 없습니다!");
        }
        return commentService.createComment(id, request, userEmail);
    }

    // 댓글 조회
    @GetMapping("/{id}/comments")
    public List<CommentResponse> getComments(@PathVariable Long id) {
        return commentService.getComments(id);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            HttpServletRequest servletRequest
    ) {
        String userEmail = (String) servletRequest.getAttribute("userEmail");
        commentService.deleteComment(postId, commentId, userEmail);
    }

    // 댓글 수정
    @PutMapping("/{postId}/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request,
            HttpServletRequest servletRequest
    ) {
        String userEmail = (String) servletRequest.getAttribute("userEmail");
        return commentService.updateComment(postId, commentId, request, userEmail);
    }

}
