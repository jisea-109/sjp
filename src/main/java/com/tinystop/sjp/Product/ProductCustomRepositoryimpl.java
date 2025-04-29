package com.tinystop.sjp.Product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tinystop.sjp.Type.ProductCategory;

import lombok.RequiredArgsConstructor;

import static com.tinystop.sjp.Product.QProductEntity.productEntity;

@RequiredArgsConstructor
public class ProductCustomRepositoryimpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override // SELECT * FROM product_table WHERE name LIKE '%name%' LIMIT getPageSize() offset getOffset() 
    public Page<ProductEntity> findByNameContainingOrderByModifiedAtDesc(String name, Pageable pageable) { // product 생성(수정) 날짜순대로 나열
        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.name.containsIgnoreCase(name))
                .orderBy(productEntity.modifiedAt.desc())
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
    public Page<ProductEntity> findProductsByComponentOrderByModifiedAtDesc(ProductCategory category, Pageable pageable) {
        List<ProductEntity> content = jpaQueryFactory
                .selectFrom(productEntity)
                .where(productEntity.component.eq(category))
                .orderBy(productEntity.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(productEntity.count())
                .from(productEntity)
                .where(productEntity.component.eq(category))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
