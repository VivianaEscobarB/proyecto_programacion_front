package com.uniquindio.alojamientosAPI.persistence.repository.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReviewCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Long> {

    List<ReviewCommentEntity> findByGuestRating_Id(Long guestRatingId);

    List<ReviewCommentEntity> findByUser_Id(Long userId);

    List<ReviewCommentEntity> findByParentComment_Id(Long parentCommentId);
}

