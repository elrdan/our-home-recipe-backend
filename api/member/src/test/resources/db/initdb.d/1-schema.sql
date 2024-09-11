# 회원
USE testdb;
CREATE TABLE IF NOT EXISTS member
(
    member_id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,         -- 회원 식별자
    email                   VARCHAR(30)     NOT NULL,                           -- 회원 이메일
    password                VARCHAR(80)     NOT NULL,                           -- 회원 비밀번호
    nickname                VARCHAR(20)     NOT NULL,                           -- 회원 닉네임
    phone_number            VARCHAR(20)     NOT NULL,                           -- 회원 전화번호
    name                    VARCHAR(20)     NOT NULL,                           -- 회원 이름
    status                  VARCHAR(20)     NOT NULL,                           -- 회원 상태
    role                    VARCHAR(20)     NOT NULL,                           -- 회원 권한
    provider                VARCHAR(20),                                        -- 회원 제공자(ex KAKAO, NAVER 등)
    created_at              DATETIME,                                           -- 회원 생성일
    created_by              VARCHAR(20),                                        -- 회원 생성자
    updated_at              DATETIME,                                           -- 회원 수정일
    updated_by              VARCHAR(20)                                         -- 회원 수정자
);