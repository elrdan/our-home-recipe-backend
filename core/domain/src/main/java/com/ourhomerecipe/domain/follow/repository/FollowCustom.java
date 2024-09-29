package com.ourhomerecipe.domain.follow.repository;

public interface FollowCustom {
	long followerCount(long memberId);

	long followingCount(long memberId);
}
