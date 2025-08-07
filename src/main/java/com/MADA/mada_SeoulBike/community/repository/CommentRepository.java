package com.MADA.mada_SeoulBike.community.repository;

import com.MADA.mada_SeoulBike.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}