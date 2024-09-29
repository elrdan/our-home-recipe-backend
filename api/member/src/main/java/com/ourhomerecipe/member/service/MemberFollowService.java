package com.ourhomerecipe.member.service;

import static com.ourhomerecipe.domain.common.error.code.FollowErrorCode.*;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhomerecipe.domain.follow.Follow;
import com.ourhomerecipe.domain.follow.repository.FollowRepository;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.member.exception.MemberException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberFollowService {
	private final FollowRepository followRepository;
	private final EntityManager entityManager;

	/**
	 * 회원 팔로우
	 */
	@Transactional
	public void follow(Long followerId, Long followingId) {
		if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw new MemberException(ALREADY_FOLLOWING);
		}

		try {
			// getReference를 사용하면 필요할 때까지 데이터베이스에서 데이터를 로드하지 않기 때문에 시스템의 성능을 최적화 할 수 있다.
			// 데이터 베이스에 식별자가 존재하지 않는 경우 프록시 초기화하는 도중 EntityNotFoundException 예외가 발생함
			Member follower = entityManager.getReference(Member.class, followerId);
			Member following = entityManager.getReference(Member.class, followingId);

			Follow follow = Follow.builder()
				.follower(follower)
				.following(following)
				.build();

			followRepository.save(follow);
		} catch (EntityNotFoundException e) {
			throw new MemberException(NOT_EXISTS_MEMBER);
		}
	}

	/**
	 * 회원 언팔로우
	 */
	@Transactional
	public void unfollow(Long followerId, Long followingId) {
		Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
			.orElseThrow(() -> new MemberException(NOT_ALREADY_FOLLOWING));

		followRepository.delete(follow);
	}
}
