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

JPA, QueryDsl, SpringBoot를 사용하여 ~~를 구현하여 백엔드 서버를 구축함. Spring MVC 특성상 JWT 대신 Session을 사용함.

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

### 1. **Transaction silently rolled back because it has been marked as rollback-only** 오류 해결

        @Transactional
        public BigDecimal getReviewAverage(Long productId) { 
            ProductEntity product = this.productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list")); // productid를 통해서 Product 찾기
            List<ReviewEntity> reviews = this.reviewRepository.findByProduct(product); // product의 리뷰 리스트 가져오기
            
            if (reviews.isEmpty()) { // product 리뷰가 제대로 가져와졌는지 체크
                throw new CustomException(REVIEW_NOT_FOUND, "product-list");
            }
            int sum = 0;
            for (int i = 0; i < reviews.size(); i++) {
                sum += reviews.get(i).getRating(); // 리뷰값 다 더하기
            }
            double average = (double) sum / reviews.size(); // 평균값 double값으로 변수 저장
            return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP); // 반올림해서 return
        }   

하나의 Product의 리뷰 평균 평점을 구하기 위해서 만든 ReviewService에 있던 함수.

        @Transactional
        public Map<Long, BigDecimal> getAverageRating(Map<Long, BigDecimal> productRatings, Page<LoadProductDto> products) {
            for (LoadProductDto product : products) {
                try {
                    BigDecimal rating = getReviewAverage(product.getId());
                    productRatings.put(product.getId(), rating);
                } catch (CustomException error) {
                    productRatings.put(product.getId(), BigDecimal.ZERO);
                }
            }
            return productRatings;
        }

getReviewAverage 함수를 통해 Product 목록에 있는 모든 product의 평균 평점을 표시하고 싶어서 이렇게 ProductService에서 함수를 만들어 보았다.
테스트를 해보기 위해서 ProductController 에서 밑에 있는 코드와 같이 실행을 해서 테스트를 해보았다.

    Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
    productRatings = productRatingService.getAverageRating(productRatings, products);

결과는 다음과 같았다.

    Transaction silently rolled back because it has been marked as rollback-only

Transaction에는 4가지 특징이 있다. 

1. 원자성 (Atomicity)	전부 성공하거나 전부 실패한다. 중간이 없음.
2. 일관성 (Consistency)	트랜잭션 시작 전과 후, 데이터 무결성이 유지된다.
3. 격리성 (Isolation)	동시에 여러 트랜잭션이 실행돼도 서로 간섭 안 한다.
4. 지속성 (Durability)	커밋된 데이터는 시스템 장애가 나도 보존된다.

여기서 1번 **원자성**에 의해서 하나라도 예외가 생기면 스프링의 기본 설정에 의해 바로 <u>**rollback-only**</u> 상태로 바뀌게 된다. 

두 함수에는 Transactional annotation이 붙어 있는데, getReviewAverage에서 예외가 생길 때 트렌젝션을 rollback-only 상태로 바꾸게 되면서 그 이후의 commit 시점에서는 무조건 롤백을 하게 된다. 

**즉, getReviewAverage을 실행시키는 리뷰 평점 평균 구하기의 최초의 트랜잭션인 getAverageRating 함수는 <u>내부 Transaction 의 rollback 상태</u>로 인해 더이상 commit으로 처리를 못하게 된다**.

알아본 해결책은 이렇게 있다.
1. **개별적인 Transaction을 실행하기** 

        @Transactional(propagation = Propagation.REQUIRES_NEW) 

    Child 함수인 getReviewAverage에 설정을 하여 별도의 Transaction으로 실행할 수 있게 설정. 하지만 이 설정도 결국 동일한 스레드에서 트렌젝션을 진행하기 때문에 REQUIRES_NEW에서 발생한 예외는 
    
    이를 호출한 트랜젝션에 전파되기 때문에 완벽하게 독립적인 트랜젝션이 아니다. 결국 똑같은 오류가 발생하게 되면서 rollback이 된다.

2. **Transaction에서 예외가 나와도 rollback 방지** 
            
        @Transactional(noRollbackFor = CustomException.class) 
    
    예외가 나와도 원자성을 예외처리 해주는 설정. 하지만 예외는 바깥으로 전달되게 해야 rollback 여부를 판단할 수가 있는데 try catch문이 있으면 예외가 바깥으로 전달되질 못해서 rollback이 안된다.
    
    Parent 함수인 getAverageRating 함수는 try catch문이 있다. try catch문을 리팩토링 하기 위해서는 Child 함수 getReviewAverage를 Optional로 변경하면 해결이 된다.

        public Map<Long, BigDecimal> getAverageRating(Map<Long, BigDecimal> productRatings, Page<LoadProductDto> products) {
            for (LoadProductDto product : products) {
                BigDecimal rating = getReviewAverage(product.getId()).orElse(BigDecimal.ZERO);
                productRatings.put(product.getId(), rating);
            }
            return productRatings;
        }

    getReviewAverage를 Optional로 바꾸게 되면 Controller에서도 코드를 리팩토링 하는건 오래 걸리진 않는다.
    
    하지만 결국 rollback을 방지한다고 해서 정확한 원인을 해결한건지 궁금해서 더 알아보았다.

3. **Transactional annotation 제거**

    **Transaction은 <u>데이터베이스의 상태를 변경시키는 작업의 단위</u>이다.**  getAverageRating 함수는 상품 목록에 있는 상품들에 대한 평균 평점 맵을 생성하는데, 결국 데이터베이스와는 직결되지 않는다.
    
    이 함수는 transactional annotation을 선언해서 처리할 이유가 없었기에 제거하였더니 정상적으로 작동한다. 이 프로젝트에서는 모든 Service 파일에 Transactional annotation을 포함한게 원인이었다.

    이후에 따로 Service 파일을 만들어서 Child 함수(getReviewAverage)는 DB에 접근하니 Transactional annotation을 붙이고 Parent 함수(getAverageRating)에는 제거해서 정리하였다.

### 2. 이미지를 가져올 때 트렌젝션 이후 product.getImagePaths를 하면 LazyInitializationException 오류 해결

-> query에서 leftjoin 문구, repo에서 entityGraph 제거해도됨. (제거하면 오는 이점도 설명)

상품이나 리뷰에 사진을 등록하면 이미지들이 DB에 저장되고 필요할 때 그 사진들을 가져와야 하는데 처음에 이런 오류가 떴다.

    org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: ~~.ProductEntity.imagePaths: could not initialize proxy - no Session

위 오류는 상품을 조회할 때 나오는 오류였고, 리뷰 또한 Entity만 다를 뿐 똑같은 오류를 가지고 있었다. 

밑에 있는 Column은 ProductEntity에 있으며 이미지 경로를 저장하고 있다. (ReviewEntity에도 이름만 다른 Column이 있다.)

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_path")
    private List<String> imagePaths = new ArrayList<>();

이 프로젝트에서는 상품이랑 리뷰의 이미지가 여러장이 될 수 있기에 ElementCollection이 붙는데, JPA의 구현체인 Hibernate는 이걸 연관 테이블에 매핑된 값 타입 컬렉션으로 처리하고 값 타입 컬렉션은 조회할 때 <u>**Lazy Loading**</u>을 사용한다. 

JPA에는 **OSIV(Open Session In View)** 이라는 방법으로 영속성 컨텍스트와 Hibernate Session을 View가 렌더링이 될 때 까지 유지시키는 방법을 채용하고 있는데,

지금 오류나는 것은 Controller에서 imagePaths를 접근할라고 할 때 이미 Service 계층에서 모든 데이터를 로딩 못하고 트랜잭션이 끝나면서 Session이 닫히게 되면서 LazyInitializationException을 발생시키게 된 것이다.

오류를 알고 나서 생각해본 해결방법은 다음과 같다.

1. FetchType을 Lazy -> Eager로 수정

        @ElementCollection(fetch = FetchType.EAGER)

    이렇게 바꾸면 Lazy Loading이 아닌 Eager Loading이 되는데, Eager Loading은 연관된 모든 객체의 데이터까지 한번에 불러오기에 성능에 영향을 줄 수가 있다. 장기적으로 봤을 땐 좋지 않은 선택이기에 다른 해결방법을 알아보았다.
2. Service 레벨에서 Hibernate Lazy 초기화

        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // 강제 초기화
        }
    Service에서 상품이나 리뷰를 조회할 때 imagePaths를 미리 강제 접근해서 세션 안에서 로딩되게 하는 방법이지만, 조회할 때 쓰이는 모든 함수에 코드를 추가해야 하기에 유지 보수면에서는 물론 가독성면에서도 좋진 않다.

3. Querydsl에서 fetchJoin 사용

4. DTO로 변환

    Dto로 변환을 해서 사용하면 Hibernate 세션이 닫히기 전에 필요한 정보만 안전하게 가져올 수 있다.

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public class LoadProductDto { // 원본엔 다른 product 정보들 포함되있음
            private List<String> imagePaths;
            public LoadProductDto(ProductEntity product) {
                this.imagePaths = List.copyOf(product.getImagePaths()); // Lazy 초기화
            }
        }
    이렇게 하여서
### 3. Querydsl searchProductByNameContaining 정확도에 고려한 요소들

4. 배포 과정
 - 도메인
 - AWS
 - Docker
