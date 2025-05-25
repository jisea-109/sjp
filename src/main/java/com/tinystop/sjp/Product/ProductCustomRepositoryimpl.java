package com.tinystop.sjp.Product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.tinystop.sjp.Order.QOrderEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Product.Category.QProductCategoryEntity;
import com.tinystop.sjp.Review.QReviewEntity;

import lombok.RequiredArgsConstructor;

import static com.tinystop.sjp.Product.QProductEntity.productEntity;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QProductEntity product = QProductEntity.productEntity;
    private final QOrderEntity order = QOrderEntity.orderEntity;
    private final QReviewEntity review = QReviewEntity.reviewEntity;
    private final QProductCategoryEntity component = QProductCategoryEntity.productCategoryEntity;

    @Override
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
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        long total = jpaQueryFactory // 데이터 전체 결과 개수
                .select(product.count())
                .from(product)
                .leftJoin(product.component, component)
                .where(product.name.containsIgnoreCase(name)
                        .or(component.name.containsIgnoreCase(name)))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductByComponent(ProductCategoryEntity component, Pageable pageable) {
        Expression<Long> totalScore = JPAExpressions
                .select(order.count().multiply(2).add( // 제품별 점수 계산 (판매량 * 2 + 리뷰 수)
                JPAExpressions.select(review.count())
                        .from(review)
                        .where(review.product.eq(product))
                ))
                .from(order)
                .where(order.product.eq(product));

        OrderSpecifier<Long> Score = new OrderSpecifier<>(Order.DESC, totalScore);

        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .where(product.component.eq(component))
                .orderBy(Score)
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); //  PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override // SELECT * FROM product_table WHERE name LIKE '%name%' LIMIT getPageSize() offset getOffset() 
    public Page<ProductEntity> searchProductsByNameSortedByCreatedAtDesc(String name, Pageable pageable) { // product 생성(수정) 날짜순대로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.name.containsIgnoreCase(name))
                .orderBy(productEntity.createdAt.desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch(); // Query List 가져오기 

        long total = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.name.containsIgnoreCase(name))
                .fetch().size();

        return new PageImpl<>(products, pageable, total); //  PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductsByComponentSortedByCreatedAtDesc(ProductCategoryEntity component, Pageable pageable) { // 선택한 component 날짜별로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .where(product.component.eq(component))
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수(?), 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductsSortedBySales(String name, Pageable pageable) { // 검색한 product 판매량 순으로 나열
        List<ProductEntity> products = jpaQueryFactory
                .select(product)
                .from(order)
                .join(order.product, product) // inner join
                .where(product.name.containsIgnoreCase(name))
                .groupBy(product.id)
                .orderBy(order.quantity.sum().desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(order)
                .join(order.product, product)
                .where(product.name.containsIgnoreCase(name))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductComponentsSortedBySales(ProductCategoryEntity component, Pageable pageable) { // 선택한 component 판매량 순으로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(order).on(order.product.eq(product)) 
                .where(product.component.eq(component))
                .groupBy(product.id)
                .orderBy(order.quantity.sum().desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(order)
                .join(order.product, product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductSortedByReviews(String name, Pageable pageable) { // 검색한 product 리뷰순으로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(review).on(review.product.eq(product))
                .groupBy(product.id)
                .orderBy(review.count().desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(review)
                .join(review.product, product)
                .where(productEntity.name.containsIgnoreCase(name))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }

    @Override
    public Page<ProductEntity> searchProductComponentsSortedByReviews(ProductCategoryEntity component, Pageable pageable) { // 선택한 component 리뷰순으로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(review).on(review.product.eq(product))
                .where(product.component.eq(component))
                .groupBy(product.id)
                .orderBy(review.count().desc())
                .offset(pageable.getOffset()) // 건너뛸 데이터 행 수, 예를 들어서 Offset(20)이면 20번째 데이터까지 스킵하고 21번째 데이터부터 시작하게 지정
                .limit(pageable.getPageSize()) // 가져올 데이터 수 (한 페이지당 몇개), 예를 들어서 limit(10)이면 10개 가져옴. ProductController에서는 10이 기본 지정
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(review)
                .join(review.product, product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total); // PageImpl<>은 PageImpl(List+Pageable) -> Page, (필터링된 데이터 (Products), 페이징 정보, 데이터 총 개수)
    }
}
