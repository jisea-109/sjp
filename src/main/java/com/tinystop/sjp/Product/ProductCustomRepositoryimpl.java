package com.tinystop.sjp.Product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tinystop.sjp.Order.QOrderEntity;
import com.tinystop.sjp.Review.QReviewEntity;
import com.tinystop.sjp.Type.ProductCategory;

import lombok.RequiredArgsConstructor;

import static com.tinystop.sjp.Product.QProductEntity.productEntity;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override // SELECT * FROM product_table WHERE name LIKE '%name%' LIMIT getPageSize() offset getOffset() 
    public Page<ProductEntity> findProductsByNameSortedByModifiedAtDesc(String name, Pageable pageable) { // product 생성(수정) 날짜순대로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.name.containsIgnoreCase(name))
                .orderBy(productEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); // Query List 가져오기 

        long total = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.name.containsIgnoreCase(name))
                .fetch().size();

        return new PageImpl<>(products, pageable, total); // (필터링된 데이터, 페이징된 데이터, 현재 페이지 정보)
    }

    @Override
    public Page<ProductEntity> findProductsByComponentSortedByModifiedAtDesc(ProductCategory component, Pageable pageable) {
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.component.eq(component))
                .orderBy(productEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(productEntity.count())
                .from(productEntity)
                .where(productEntity.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductEntity> searchProductsSortedBySales(String name, Pageable pageable) {

        QOrderEntity order = QOrderEntity.orderEntity;
        QProductEntity product = QProductEntity.productEntity;

        List<ProductEntity> products = jpaQueryFactory
                .select(product)
                .from(order)
                .join(order.product, product) // inner join
                .where(product.name.containsIgnoreCase(name))
                .groupBy(product.id)
                .orderBy(order.quantity.sum().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(order)
                .join(order.product, product)
                .where(product.name.containsIgnoreCase(name))
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductEntity> searchProductComponentsSortedBySales(ProductCategory component, Pageable pageable) {

        QOrderEntity order = QOrderEntity.orderEntity;
        QProductEntity product = QProductEntity.productEntity;

        List<ProductEntity> products = jpaQueryFactory
            .select(product)
            .from(order)
            .join(order.product, product)
            .where(product.component.eq(component))
            .groupBy(product.id)
            .orderBy(order.quantity.sum().desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(order)
                .join(order.product, product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductEntity> searchProductSortedByReviews(String name, Pageable pageable) {

        QReviewEntity review = QReviewEntity.reviewEntity;
        QProductEntity product = QProductEntity.productEntity;

        List<ProductEntity> products = jpaQueryFactory
                .select(product)
                .from(product)
                .leftJoin(review).on(review.product.eq(product))
                .groupBy(product.id)
                .orderBy(review.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(review)
                .join(review.product, product)
                .where(productEntity.name.containsIgnoreCase(name))
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductEntity> searchProductComponentsSortedByReviews(ProductCategory component, Pageable pageable) {

        QReviewEntity review = QReviewEntity.reviewEntity;
        QProductEntity product = QProductEntity.productEntity;

        List<ProductEntity> products = jpaQueryFactory
            .select(product)
            .from(product)
            .leftJoin(review).on(review.product.eq(product))
            .where(product.component.eq(component))
            .groupBy(product.id)
            .orderBy(review.count().desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = jpaQueryFactory
                .select(product.countDistinct())
                .from(review)
                .join(review.product, product)
                .where(product.component.eq(component))
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }
}
