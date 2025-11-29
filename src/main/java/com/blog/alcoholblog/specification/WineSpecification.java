package com.blog.alcoholblog.specification;

import com.blog.alcoholblog.dto.WineSearchCriteriaDTO;
import com.blog.alcoholblog.model.Wine;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class WineSpecification {

    public static Specification<Wine> wineSpecification(WineSearchCriteriaDTO criteriaDTO) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // --- STRING FIELDS (LIKE) ---
            addLikeIfPresent(predicates, cb, root.get("name"), criteriaDTO.name());
            addLikeIfPresent(predicates, cb, root.get("color"), criteriaDTO.color());
            addLikeIfPresent(predicates, cb, root.get("winery"), criteriaDTO.winery());
            addLikeIfPresent(predicates, cb, root.get("kind"), criteriaDTO.kind());
            addLikeIfPresent(predicates, cb, root.get("country"), criteriaDTO.country());
            addLikeIfPresent(predicates, cb, root.get("region"), criteriaDTO.region());

            // --- EXACT MATCH ---
            if (criteriaDTO.year() != null) {
                predicates.add(cb.equal(root.get("year"), criteriaDTO.year()));
            }

            // --- SCORE RANGE ---
            if (criteriaDTO.minScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("score"), criteriaDTO.minScore()));
            }
            if (criteriaDTO.maxScore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("score"), criteriaDTO.maxScore()));
            }

            // --- ALCOHOL RANGE ---
            if (criteriaDTO.minAlcohol() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("alcohol"), criteriaDTO.minAlcohol()));
            }
            if (criteriaDTO.maxAlcohol() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("alcohol"), criteriaDTO.maxAlcohol()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addLikeIfPresent(List<Predicate> predicates,
                                         jakarta.persistence.criteria.CriteriaBuilder cb,
                                         jakarta.persistence.criteria.Path<String> field,
                                         String value) {
        if (StringUtils.hasText(value)) {
            String pattern = "%" + value.toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(field), pattern));
        }
    }
}
