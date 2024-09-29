package com.ourhomerecipe.domain.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.follow.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowCustom {
	// 팔로우 관계의 존재 여부를 확인하는 메서드
	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

	// 팔로우 조회
	Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
