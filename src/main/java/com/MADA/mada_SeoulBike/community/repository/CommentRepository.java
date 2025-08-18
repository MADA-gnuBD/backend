package com.MADA.mada_SeoulBike.community.repository;

import com.MADA.mada_SeoulBike.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    // ✅ 게시글의 모든 댓글 삭제 (필드가 post 참조든 postId 값이든 상관없이 동작)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from Comment c where c.post.id = :postId")
    int deleteAllByPostId(@Param("postId") Long postId);
}