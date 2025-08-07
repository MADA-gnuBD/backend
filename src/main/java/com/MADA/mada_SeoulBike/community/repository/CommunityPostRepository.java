package com.MADA.mada_SeoulBike.community.repository;

import com.MADA.mada_SeoulBike.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByCategory(String category);
    List<CommunityPost> findByAuthorId(String authorId);
    // 제목/내용 검색 (프론트 검색용)
    List<CommunityPost> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}

