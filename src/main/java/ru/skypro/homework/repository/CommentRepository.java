package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.entity.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByAdPk(int adPk);

    @Query("SELECT c FROM Comment c WHERE c.ad.pk = :adId AND c.pk = :commentId")
    Comment findByAdIdAndCommentId(@Param("adId") int adId, @Param("commentId") int commentId);

    void deleteByAdPkAndPk(int adPk, int pk);
}