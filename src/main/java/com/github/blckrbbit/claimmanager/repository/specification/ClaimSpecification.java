package com.github.blckrbbit.claimmanager.repository.specification;

import com.github.blckrbbit.claimmanager.repository.entity.Claim;
import org.springframework.data.jpa.domain.Specification;



public class ClaimSpecification {

    public static Specification<Claim> userLoginLike(String loginPart) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(root.get("userFrom").get("login"), String.format("%%%s%%", loginPart));
    }

    public static Specification<Claim> statusEqual(String status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("status"), status.toUpperCase());
    }

    public static Specification<Claim> statusNotDraft() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .notEqual(root.get("status"), "DRAFT");
    }

    public static Specification<Claim> statusAccepted() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("status"), "ACCEPTED");
    }

    public static Specification<Claim> statusRejected() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("status"), "REJECTED");
    }

    public static Specification<Claim> statusSent() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("status"), "SENT");
    }

    public static Specification<Claim> userIdEqual(Long userId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("userFrom").get("id"), userId);
    }

    public static Specification<Claim> userLoginEqual(String userLogin) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("userFrom").get("login"), userLogin);
    }

    public static Specification<Claim> claimIdEqual(Long claimId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("id"), claimId);
    }
}
