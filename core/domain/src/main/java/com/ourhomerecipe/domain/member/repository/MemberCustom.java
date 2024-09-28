package com.ourhomerecipe.domain.member.repository;

import com.ourhomerecipe.dto.member.response.MemberMyProfileResDto;

public interface MemberCustom {
	MemberMyProfileResDto getMeProfile(long id);
}
