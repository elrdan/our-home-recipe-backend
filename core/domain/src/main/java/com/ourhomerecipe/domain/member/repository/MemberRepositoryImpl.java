package com.ourhomerecipe.domain.member.repository;

import static com.ourhomerecipe.domain.member.QMember.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.member.response.MemberMyProfileResDto;
import com.ourhomerecipe.dto.member.response.MemberSearchResDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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

	/**
	 * 회원 검색(닉네임)
	 * 대소문자 구분 안함
	 */
	@Override
	public Page<MemberSearchResDto> findByNicknameContaining(Pageable pageable, long currentMemberId, String nickname) {
		List<MemberSearchResDto> memberList = queryFactory
			.select(fields(MemberSearchResDto.class,
				member.id,
				member.nickname
			))
			.from(member)
			.where(member.nickname.containsIgnoreCase(nickname)	// 대소문자 구분없이 닉네임 ex) %nickname%
				.and(member.id.ne(currentMemberId)))			// 자기 자신을 제외하는 조건
			.orderBy(new CaseBuilder()
				.when(member.nickname.eq(nickname)).then(0)
				.otherwise(1)
				.asc()) // 정확히 일치하는 경우를 우선으로 정렬
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(queryFactory
			.select(member.count())
			.from(member)
			.where(member.nickname.containsIgnoreCase(nickname)
				.and(member.id.ne(currentMemberId)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(memberList, pageable, totalCount);
	}

	private BooleanExpression memberId(Long id) {
		return member.id.eq(id);
	}
}
