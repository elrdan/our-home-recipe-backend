package com.ourhomerecipe.domain.member.repository;

import static com.ourhomerecipe.domain.member.QMember.*;
import static com.querydsl.core.types.Projections.*;

import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.member.response.MemberMyProfileResDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustom {
	private final JPAQueryFactory queryFactory;

	/**
	 * 내 프로필 조회
	 */
	@Override
	public MemberMyProfileResDto getMeProfile(long id) {
		return queryFactory
			.select(fields(MemberMyProfileResDto.class,
				member.id,
				member.email,
				member.nickname,
				member.phoneNumber,
				member.name,
				member.profileImage,
				member.introduce
			))
			.from(member)
			.where(memberId(id))
			.fetchOne();
	}

	private BooleanExpression memberId(Long id) {
		return member.id.eq(id);
	}
}
