## 목차
1. 프로그램 소개
2. ERD
3. 세부 구성도
4. 구현 내용
5. UI 모음
6. 프로젝트 세부 주제
# 프로젝트 소개
컴퓨터 부품들을 검색하고 주문할 수 있는 온라인 쇼핑몰 개인 프로젝트입니다.

백엔드 서버 구축부터 시작해서 배포까지 함으로써 공부를 위해 백엔드에 집중한 Spring MVC 웹 어플리케이션 프로젝트이며, 프론트엔드 또한 백엔드의 기능이 구현됨을 확인하기 위해 최소한의 기능이 구현되어 있습니다.

개발기간: 25년 03/09 ~ 05/16

| **기술 스택** ||
| ----- | ----- |
| **프론트**: HTML, CSS, Javascript |
| **백엔드**: SpringBoot, JPA, Querydsl, Mysql | 
| **Deploy**: Gradle, AWS, Docker |

JPA, QueryDsl, SpringBoot를 사용하여 ~~를 구현하여 백엔드 서버를 구축함.

Spring MVC 특성상 JWT 대신 Session을 사용함.

상품 검색 결과 정확도를 높히기 위해 Querydsl을 사용.

# ERD
[ERD 링크](https://www.erdcloud.com/d/eE4zwgLhMPij5dE7C)
![Image](https://github.com/user-attachments/assets/7d70023b-17c0-4564-bc4d-7ebf7c4c2f15)


# 세부 구성도


# 구현 내용
1. 검색 기능 + 페이징 기능
2. 로그인 기능
 - 이메일 인증
 - Spring Security
 - BCrypt를 통한 비밀번호 암호화
3. 마이 페이지 기능
 - 비밀번호 변경 기능
 - 카트에 담은 상품, 주문했던 상품, 리뷰 리스트 확인 기능
4. 리뷰, 상품, 카트, 주문 CRUD 기능

# UI 모음

# 프로젝트 세부 주제

1. getReviewAverage 함수를 사용했을 때 **Transaction silently rolled back because it has been marked as rollback-only** 이라는 오류가 뜨는 이유
Transactional annotation 에 대해서 말하고 getAverageRating 함수도 그 annotation을 사용하고 있었음을 얘기. 그 다음 Transacion에 대해서 설명을 하며 오류가 났었던 원인을 말한 뒤 해결법 제시.

2. 이미지를 가져올 때 트렌젝션 이후 product.getImagePaths를 하면 Hibernate Lazy를 방지해야 하는 이유 설명 + DTO로 해결 + org.hibernate.LazyInitializationException: could not initialize proxy - no Session 오류 설명 
-> query에서 leftjoin 문구, repo에서 entityGraph 제거해도됨. (제거하면 오는 이점도 설명)

3. Querydsl searchProductByNameContaining 정확도에 고려한 요소들

4. 배포 과정
 - 도메인
 - AWS
 - Docker
