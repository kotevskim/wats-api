package com.sorsix.interns.finalproject.wats.service.impl;

import com.sorsix.interns.finalproject.wats.domain.Location;
import com.sorsix.interns.finalproject.wats.domain.User;
import com.sorsix.interns.finalproject.wats.domain.UserDTO;
import com.sorsix.interns.finalproject.wats.domain.review.Review;
import com.sorsix.interns.finalproject.wats.domain.review.ReviewComment;
import com.sorsix.interns.finalproject.wats.persistence.ReviewCommentDAO;
import com.sorsix.interns.finalproject.wats.persistence.ReviewDAO;
import com.sorsix.interns.finalproject.wats.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDAO reviewDAO;
    private final ReviewCommentDAO reviewCommentDAO;

    @Autowired
    public ReviewServiceImpl(ReviewDAO reviewDAO,
                             ReviewCommentDAO reviewCommentDAO) {
        this.reviewDAO = reviewDAO;
        this.reviewCommentDAO = reviewCommentDAO;
    }

    @Override
    public Optional<Review> findReview(Long id) {
        return reviewDAO.findById(id);
    }

    @Override
    public Optional<ReviewComment> findReviewComment(Long commentId) {
        return this.reviewCommentDAO.findById(commentId);
    }

    @Override
    public Page<Review> getReviewsForLocation(Long locationId, Pageable pageable) {
        return reviewDAO.findByLocationId(locationId, pageable);
    }

    @Override
    public Page<ReviewComment> getReviewComments(Long reviewId, Pageable pageable) {
        return reviewCommentDAO.findByReviewId(reviewId, pageable);
    }

    @Override
    public Review createReview(User user, Location location, String description) {
        LocalDateTime datePublished = LocalDateTime.now();
        Review review = new Review(description, datePublished, user, location);
        Review r = reviewDAO.save(review);
        LOGGER.info("Created review with id: {}, for location [id: {}, name: {}]",
                r.getId(), r.getLocation().getId(), r.getLocation().getName());
        return r;
    }

    @Override
    public Collection<ReviewComment> getTopCommentsForReview(Long reviewId, int limit) {
        return reviewCommentDAO.findTopByQuestionId(reviewId, limit);
    }

    @Override
    public ReviewComment createReviewComment(User user, Review review, String description) {
        LocalDateTime datePublished = LocalDateTime.now();
        ReviewComment c = this.reviewCommentDAO.save(new ReviewComment(user, review, description, datePublished));
        LOGGER.info("Created comment with id: {}, for review with id: {}", c.getId(), c.getReview().getId());
        return c;
    }

    @Override
    public Collection<User> mapReviewLikesToUsers(Review review) {
        return review.getLikes();
    }

    @Override
    public Collection<User> mapReviewCommentLikesToUsers(ReviewComment comment) {
        return comment.getLikes();
    }

    @Override
    @Transactional
    public boolean postLikeForReview(Review review, User user) {
        boolean liked = postLike(review.getLikes(), user);
        if (!liked) {
		LOGGER.info("Removed like for review with id: {}, from user with id: {}",
                    review.getId(), user.getId());
            
        } else {
            LOGGER.info("Posted like for review with id: {}, from user with id: {}",
                    review.getId(), user.getId());
        }
        return liked;
    }

    @Override
    @Transactional
    public boolean postLikeForReviewComment(ReviewComment comment, User user) {
        boolean liked = postLike(comment.getLikes(), user);
        if (!liked) {
	    LOGGER.info("Removed like for comment with id: {}, from user with id: {}",
                comment.getId(), user.getId());    
        } else {
	    LOGGER.info("Posted like for comment with id: {}, from user with id: {}",
                comment.getId(), user.getId());	
            
        }
        return liked;
    }

    private boolean postLike(Set<User> likers, User user) {
        boolean liked = likers.contains(user);
        if (liked) likers.remove(user);
	else  likers.add(user);
	return !liked;
    }

}
