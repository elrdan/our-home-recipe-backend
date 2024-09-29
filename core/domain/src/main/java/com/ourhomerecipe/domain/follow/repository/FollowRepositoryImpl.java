package com.ourhomerecipe.domain.follow.repository;

import static com.ourhomerecipe.domain.follow.QFollow.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public long followerCount(long memberId) {
		return Optional.ofNullable(queryFactory
				.select(follow.count())
				.from(follow)
				.where(follow.follower.id.eq(memberId))
				.fetchOne())
			.orElse(0L);
	}

	@Override
	public long followingCount(long memberId) {
		return Optional.ofNullable(queryFactory
				.select(follow.count())
				.from(follow)
				.where(follow.following.id.eq(memberId))
				.fetchOne())
			.orElse(0L);
	}
}
