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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService postService;
    private final CommentService commentService;

    // 목록(전체/카테고리/작성자/검색)
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
        String email = (String) request.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
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
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        return postService.updatePost(id, req, email);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        postService.deletePost(id, email);
    }

    // 좋아요
    @PostMapping("/{id}/like")
    public PostResponse likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    // 댓글 등록
    @PostMapping("/{id}/comments")
    public CommentResponse createComment(
            @PathVariable Long id,
            @RequestBody CreateCommentRequest req,
            HttpServletRequest servletRequest
    ) {
        String email = (String) servletRequest.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        return commentService.createComment(id, req, email);
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
        String email = (String) servletRequest.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        commentService.deleteComment(postId, commentId, email);
    }

    // 댓글 수정
    @PutMapping("/{postId}/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest req,
            HttpServletRequest servletRequest
    ) {
        String email = (String) servletRequest.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) email = auth.getName();
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        return commentService.updateComment(postId, commentId, req, email);
    }
}

