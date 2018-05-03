package com.sorsix.interns.finalproject.wats.persistence;

import com.sorsix.interns.finalproject.wats.domain.Review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewDao extends JpaRepository<Review, Long> {

    Optional<Review> findByLocationId(Long id);
}
