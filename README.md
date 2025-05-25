## 목차
1. 프로그램 소개
2. ERD
3. 세부 구성도
4. 구현 내용 + UI 모음
6. 프로젝트 세부 주제
# 프로젝트 소개
컴퓨터 부품들을 검색하고 주문할 수 있는 온라인 쇼핑몰 개인 프로젝트입니다.

백엔드 서버 구축부터 시작해서 배포까지 함으로써 공부를 위해 백엔드에 집중한 Spring MVC 웹 어플리케이션 프로젝트이며, 프론트엔드 또한 백엔드의 기능이 구현됨을 보여주기 위해 최소한의 기능이 구현되어 있습니다.

개발기간: 25년 03/09 ~ 05/16

| **기술 스택** |
| ----- |
| **프론트**: HTML, CSS, Javascript, Thymeleaf |
| **백엔드**: SpringBoot, JPA, Querydsl, Mysql | 
| **Deploy**: Gradle, AWS, Docker |

JPA, QueryDsl, SpringBoot를 사용하여 백엔드 서버를 구축하였고 Docker, AWS 등을 이용해 서버를 배포.

Spring MVC을 사용하고 있기에 Session 기반 인증을 사용함.

상품 검색 결과 정확도를 높히기 위해 Querydsl을 사용.

# ERD
[ERD 링크](https://www.erdcloud.com/d/eE4zwgLhMPij5dE7C)
![Image](https://github.com/user-attachments/assets/7d70023b-17c0-4564-bc4d-7ebf7c4c2f15)


# 세부 구성도


# 구현 내용 + UI 모음
### 1. 검색 기능 + 페이징 기능

![Image](https://github.com/user-attachments/assets/2988c87e-57ff-465a-969d-bbb467134cb2)

메인 페이지에서 상품을 검색할 수 있는 기능, 그리고 검색창 밑에 있는 component를 클릭하면 그 component 에 맞는 상품들이 나열됩니다.

상품을 검색할 때 또는 Component를 클릭할 때는 여러가지 요소들을 감안해서 적합한 결과를 내게끔 했다. [링크](#3-querydsl-searchproductbynamecontaining-정확도에-고려한-요소들)

### 2. 로그인 기능

 - 회원가입 + 이메일 인증

    ![Image](https://github.com/user-attachments/assets/bca4b1b1-9022-4ea7-b8ea-b5c7128977ea)

    양식에 맞는 아이디, 비밀번호, 그리고 이메일까지 입력하면 이메일 인증을 받아야 한다. 

    ![Image](https://github.com/user-attachments/assets/a0d32826-031d-4a65-a9fd-957dbf8f79f1)

    인증번호 전송을 누르면 이메일에 이렇게 6개의 알파벳과 숫자가 섞인 인증번호가 도착하게 되는데, 이 코드를 입력해주면 성공적으로 회원가입이 된다.

 - Spring Security

            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL); // SecurityContext가 현재 실행 중인 스레드에만 저장 (기본상태)

            http.authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers(HttpMethod.GET,PermittedGET).permitAll() // GET method 허용
                    .requestMatchers(HttpMethod.POST,PermittedPOST).permitAll() // POST method 허용
                    .requestMatchers(AfterAuthenticatedGET).authenticated() // 로그인 했을 시 GET method 허용
                    .requestMatchers(AfterAuthenticatedPOST).authenticated() // 로그인 했을 시 POST method 허용
                    .requestMatchers(BePermittedAdmin).hasRole("ADMIN") // admin 허용
                    .anyRequest().authenticated() // 나머진 권한 필요
                )
    Get, Post 들을 따로 정리해서 비로그인 허용, 로그인 필요, 관리자 전용 변수로 정리를 하였다.
    
    기본적으로 인증된 사용자만 접근 가능하도록 설정하였지만 이 프로젝트에 있는 모든 메소드들은 다 추가를 해놓았다.

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //필요한 경우 세션 생성 Always로 할 시 모든 요청마다 세션 생성, STATELESS는 REST API용
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession()) // 로그인 할 때마다 새로운 세션 ID 발급 (세션 고정 공격 방지)
                .maximumSessions(1) // 한 유저당 1 세션
                .maxSessionsPreventsLogin(true) // 1 세션 넘으면 다른 로그인 차단
                .expiredUrl("/login?expired") // 세션 만료되면 여기 페이지로 다이렉트함. 30분 후 만료
            )
    
    세션은 사용자당 한 개의 세션만 사용하게끔 제한하였고, 로그인 할 때마다 새로운 세션 ID 발급 함으로써 세션 고정 공격 방지를 함.

    30분이 지나면 자동으로 로그인 화면으로 리다이이렉션 되도록 설정함.

            .securityContext(securityContext -> securityContext
            .securityContextRepository(securityContextRepository) // SecurityContext를 세션에 저장
            )
            .formLogin(form -> form // 로그인
                .loginPage("/signinPage") 
                .defaultSuccessUrl("/main.html", true)   
                .failureUrl("/signinPage?error=true") 
            .permitAll()
            )
            .logout(logout -> logout // 로그아웃
                .logoutUrl("/signout")
                .logoutSuccessUrl("/main.html")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID") // 로그아웃 후 세션 삭제
            );
    인증 정보는 세션 기반으로 저장되며 (SecurityContextRepository) 로그인 성공 여부에 따른 페이지와 로그아웃을 하면서 리다이렉션 하는 페이지 설정과 함께 
    
    세션 무효화 및 쿠키 삭제를 하며 완전 로그아웃을 하게끔 한다.

 - BCrypt를 통한 비밀번호 암호화
### 3. 마이 페이지 기능
 - 비밀번호 변경 기능
 - 카트에 담은 상품, 주문했던 상품, 리뷰 리스트 확인 기능
### 4. 리뷰, 상품, 카트, 주문 CRUD 기능

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
#### 1. 개별적인 Transaction을 실행하기

    @Transactional(propagation = Propagation.REQUIRES_NEW) 

Child 함수인 getReviewAverage에 설정을 하여 별도의 Transaction으로 실행할 수 있게 설정. 하지만 이 설정도 결국 동일한 스레드에서 트렌젝션을 진행하기 때문에 REQUIRES_NEW에서 발생한 예외는 

이를 호출한 트랜잭션에 전파되기 때문에 완벽하게 독립적인 트랜잭션이 아니다. 결국 똑같은 오류가 발생하게 되면서 rollback이 된다.

#### 2. Transaction에서 예외가 나와도 rollback 방지
            
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

#### 3. Transactional annotation 제거

**Transaction은 <u>데이터베이스의 상태를 변경시키는 작업의 단위</u>이다.**  getAverageRating 함수는 상품 목록에 있는 상품들에 대한 평균 평점 맵을 생성하는데, 결국 데이터베이스와는 직결되지 않는다.

이 함수는 transactional annotation을 선언해서 처리할 이유가 없었기에 제거하였더니 정상적으로 작동한다. 이 프로젝트에서는 모든 Service 파일에 Transactional annotation을 포함한게 원인이었다.

이후에 따로 Service 파일을 만들어서 Child 함수(getReviewAverage)는 DB에 접근하니 Transactional annotation을 붙이고 Parent 함수(getAverageRating)에는 제거해서 정리하였다.

<hr>

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

#### 1. FetchType을 Lazy -> Eager로 수정

        @ElementCollection(fetch = FetchType.EAGER)

이렇게 바꾸면 Lazy Loading이 아닌 Eager Loading이 되는데, Eager Loading은 연관된 모든 객체의 데이터까지 한번에 불러오기에 성능에 영향을 줄 수가 있다. 장기적으로 봤을 땐 좋지 않은 선택이기에 다른 해결방법을 알아보았다.
#### 2. Service 레벨에서 Hibernate Lazy 초기화

        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // 강제 초기화
        }
Service에서 상품이나 리뷰를 조회할 때 imagePaths를 미리 강제 접근해서 세션 안에서 로딩되게 하는 방법이지만, 조회할 때 쓰이는 모든 함수에 코드를 추가해야 하기에 유지 보수면에서는 물론 가독성면에서도 좋진 않다. 임시 방편으로 함수가 작동하는지만 테스트할 때 잠깐 쓰고 바로 삭제하였다.

#### 3. Querydsl에서 @EntityGraph 또는 fetchJoin 사용

Lazy Loading로 설정되어 있는 Entity의 설정을 바꾸지 않고 데이터를 조회 후 실제 데이터를 즉시 가져오도록 Eager Loading 처리할 수 있는 설정이다.
EntityGraph를 추가하게 되면 fetchJoin을 추가하게 되면 연관된 엔티티나 컬렉션을 한 번에 같이 조회할 수 있게 된다.

    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> findProductsByNameSortedByModifiedAtDesc(String name, Pageable pageable);

Jpa Repository 매소드위에 추가하게 되면서 메소드를 통해 조회할 때 imagePaths는 Eager로 동작한다.

하지만 Spring Data JPA의 메소드 명 기반 쿼리에서는 EntityGraph가 효과가 있지만 이 프로젝트에서는 imagePaths를 가져올 때 QueryDsl을 쓰고 있어서 EntityGrpah가 무시된다. 

대신 Query에 직접 fetchJoin을 추가해야만 제대로 작동한다.

    public Page<ProductEntity> findProductsByComponentSortedByModifiedAtDesc(ProductCategory component, Pageable pageable) {
        List<ProductEntity> products = jpaQueryFactory
        .selectFrom(productEntity)
        .distinct()
        .leftJoin(productEntity.imagePaths).fetchJoin()
        ~~~
        .fetch();
    }

.leftJoin(productEntity.imagePaths).fetchJoin() 이 문장을 통해서 ProductEntity와 imagePaths 컬렉션을 한 번에 조회를 하게 되면서 Hibernate Lazy 방지를 하며 image를 가져올 수 있다.

이렇게 해결은 될 수 있지만 fetchJoin는 sql에서 inner join와 같은 역할을 하게 되어 중복 데이터가 발생할 수 있어서 'distinct'를 넣어서 중복 데이터를 없애줘야 한다.

그리고 2번과 같은 이유로 다른 Query를 만들 때 매번 추가를 해야 하고 가독성도 떨어져서 일단 보류하고 다른 방법을 찾아보았다.
    
#### 4. DTO로 변환

그 전까지는 Entity를 Lazy Loading 필드에 그대로 가지고 왔었기에 LazyInitializationException이 발생을 하였다.

    클라이언트에서 product를 검색할 때 제일 최우선으로 사용되는 함수
    public Page<ProductEntity> getProductsByName(String productName, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findAllByNameContaining(productName, pageable); 

        if (productName == "" || productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        return productList; // -> entity 그대로 리턴
    }

하지만 Dto로 변환을 해서 사용하면 Hibernate 세션이 닫히기 전에 필요한 정보만 추출해서 가져올 수 있기에 LazyInitializationException을 예방할 수 있다.

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class LoadProductDto {
        private List<String> imagePaths;
        public LoadProductDto(ProductEntity product) {
            this.imagePaths = List.copyOf(product.getImagePaths()); // Lazy 초기화
        }
    }
    
DTO로 다른 필요한 정보들까지 같이 포함해서 Hibeernate Lazy Loading 문제를 피하며 프론트에 넘기게 된다.

    public Page<LoadProductDto> getProductsByName(String name, Pageable pageable) { // Page<ProductEntity> -> Page<LoadProductDto> 로 변경
    Page<ProductEntity> productList = productRepository.searchProductByNameContaining(name, pageable);

    if (name == "" || productList.isEmpty()) {
        throw new CustomException(PRODUCT_NOT_FOUND,"main");
    }
    
    return productList.map(LoadProductDto::new); // LoadProductDto을 리턴
}

Repository를 통해서 Lazy 필드를 미리 로딩한 상태의 ProductEntity들을 가져오고 productList.map(LoadProductDto::new) 이 부분이 미리 가져온 필요한 데이터들을 DTO로 반환하였다.

이렇게 해서 트랜잭션이 종료되기 전 필요한 데이터를 DTO로 변환해서 이미지를 오류 없이 다 가져오게끔 하였다.

<hr>

### 3. Querydsl searchProductByNameContaining 정확도에 고려한 요소들

상품을 검색할 때 이름만 검색해서 상품을 찾기에는 등록된 상품의 개수에 따라서 정확도를 더 고려해야 유저가 원하는 결과를 가져올 수 있다.

다음은 상품을 검색할 때 쓰이는 함수 searchProductByNameContaining의 일부분이다.

    public Page<ProductEntity> searchProductByNameContaining(String name, Pageable pageable) {  // product 검색했을 때 나열
        String[] keywords = name.trim().split("\\s+");
        BooleanBuilder builder = new BooleanBuilder(); 
        for (String keyword : keywords) { // where 조건 build
                builder.and(product.name.containsIgnoreCase(keyword)
                        .or(component.name.containsIgnoreCase(keyword)));
        }

        Expression<Long> orderCount = JPAExpressions
                .select(order.count())
                .from(order)
                .where(order.product.eq(product));
        
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(product.component, component).fetchJoin()
                .where(builder)
                .orderBy(new CaseBuilder() // SQL CASE, WHEN, THEN 
                        .when(product.name.equalsIgnoreCase(name)).then(4)
                        .when(component.name.containsIgnoreCase(name)).then(3)
                        .when(product.name.startsWithIgnoreCase(name)).then(2)
                        .when(product.name.containsIgnoreCase(name)).then(1)
                        .otherwise(0)
                        .add(orderCount)
                        .desc(),
                        product.name.asc()) // 동일 점수 내에서는 이름 순 정렬
                ~~
    }

정확도를 위해 먼저 단어를 공백 단위로 쪼개서 키워드 배열을 생성했다. e.g. i5 9600 cpu-> ["i5", "9600", "cpu"]

    String[] keywords = name.trim().split("\\s+");

그리고 각 키워드가 어디 entity에 포함되는지를 조건으로 추가하는데, 이 프로젝트에서는 **<u>상품 이름(Product.name), 부품 이름(ProductCategoryEntity component.name)</u>** 이다

    BooleanBuilder builder = new BooleanBuilder(); 
            for (String keyword : keywords) { // where 조건 build
                    builder.and(product.name.containsIgnoreCase(keyword) // 상품 이름
                            .or(component.name.containsIgnoreCase(keyword))); // 부품 이름 (e.g. CPU, GPU, RAM 등)
            }
키워드 조건을 추가하고 이제 ProductEntity를 기준으로 select를 시작하는데, ProductEntity의 component와 left join을 해서 component도 가져오고, 전에 BooleanBuilder로 만든 키워드 조건을 where 에 적용한다.

    List<ProductEntity> products = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.component, component).fetchJoin()
            .where(builder)
            .groupBy(product)

판매량도 고려대상에 포함되기에 서브쿼리를 만들어서 각 상품마다 관련된 판매량의 개수를 셀 수 있게끔 하였다.

    Expression<Long> orderCount = JPAExpressions
        .select(order.count())
        .from(order)
        .where(order.product.eq(product));

마지막으로 우선순위 정렬을 설정했다. 우선순위를 점수로 매겨서 다음과 같이 설정했다.

**단어가 정확히 일치하는 경우 > component에 이름이 포함 > 단어가 시작부분이랑 일치 > 문자열에 일부 포함** 순으로 점수 주고, 점수 높은 순으로 정렬.

점수가 동일한 경우에는, 서브쿼리로 계산된 판매량을 기준으로 정렬하여 판매량이 높은 상품을 우선적으로 나열. 판매량과 점수도 같으면 이름에 따라 오름차순으로 설정.

    .orderBy(new CaseBuilder() // SQL CASE, WHEN, THEN 
            .when(product.name.equalsIgnoreCase(name)).then(4)
            .when(component.name.containsIgnoreCase(name)).then(3)
            .when(product.name.startsWithIgnoreCase(name)).then(2)
            .when(product.name.containsIgnoreCase(name)).then(1)
            .otherwise(0)
            .add(orderCount)
            .desc(),
            product.name.asc())

전부 대소문자 구분 없이 찾게끔 했다.

어떤 상품들은 소켓을 보유한 상품도 있기에 키워드에 소켓도 포함이 될 시 적용해 보는 것도 검토를 해봐야겠다.

<hr>

4. 배포 과정
 - 도메인
 - AWS
 - Docker
