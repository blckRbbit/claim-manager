package com.github.blckrbbit.claimmanager.service;

import com.github.blckrbbit.claimmanager.repository.ClaimRepository;
import com.github.blckrbbit.claimmanager.repository.entity.Claim;
import com.github.blckrbbit.claimmanager.repository.specification.ClaimSpecification;
import com.github.blckrbbit.claimmanager.util.JwtTokenUtil;
import com.github.blckrbbit.claimmanager.util.component.ClaimStatus;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ClaimStatusException;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.github.blckrbbit.claimmanager.util.component.ClaimStatus.ACCEPTED;
import static com.github.blckrbbit.claimmanager.util.component.ClaimStatus.REJECTED;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final UserService userService;
    private final ClaimRepository repository;
    private final LocalDateTime now = LocalDateTime.now();

    public String createDraftClaim(String description, HttpServletRequest request) {
        var user = userService.getUser(request);
        var claim = new Claim();
        claim.setUserFrom(user);
        claim.setCreatedAt(now);
        claim.setDescription(description);
        claim.setStatus(ClaimStatus.DRAFT);
        var savedClaim = repository.save(claim);
        Long id = savedClaim.getId();
        return id != null ? String.format("The draft claim with id %s was taking successfully", id) :
                "Something went wrong";
    }

    public Claim sendClaim(Long id, HttpServletRequest request) {
        var claim = readClaimForUser(id, request);

        if (!claim.getStatus().equals(ClaimStatus.DRAFT)) {
            throw new ClaimStatusException("You can only send claims that have DRAFT status");
        }
        changeClaimStatus(claim, ClaimStatus.SENT);
        return repository.save(claim);
    }

    public Claim readClaimForUser(Long id, HttpServletRequest request) {
        var claims = userService.getUser(request).getClaims();
        return claims.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Page<Claim> getClaimsPageForUser(HttpServletRequest request, boolean reverseOrder, int page) {
        var user = userService.getUser(request);
        List<Specification<Claim>> temp = List.of(ClaimSpecification.userIdEqual(user.getId()));
        return buildClaimsPageRequest(temp, reverseOrder, page);
    }

    public Page<Claim> getClaimsPageForOperator(String userLogin, Long id, boolean reverseOrder, int page) {
        List<Specification<Claim>> temp = new LinkedList<>();
        temp.add(ClaimSpecification.statusSent());
        if (id != null) temp.add(ClaimSpecification.claimIdEqual(id));
        if (userLogin != null) temp.add(ClaimSpecification.userLoginLike(userLogin));
        return buildClaimsPageRequest(temp, reverseOrder, page);
    }

    public Page<Claim> readAllProcessedClaims(HttpServletRequest request, String userLogin, String status,
                                              boolean reverseOrder, int page) {

        List<Specification<Claim>> temp = new LinkedList<>();
        temp.add(ClaimSpecification.statusNotDraft());
        if (userLogin != null) temp.add(ClaimSpecification.userLoginEqual(userLogin));
        if (status != null) temp.add(ClaimSpecification.statusEqual(status));
        return buildClaimsPageRequest(temp, reverseOrder, page);
    }

    private Page<Claim> buildClaimsPageRequest(Collection<Specification<Claim>> specifications,
                                               boolean reverseOrder, int page) {
        Specification<Claim> specification = Specification.where(null);
        for (Specification<Claim> subject : specifications) {
            specification = specification.and(subject);
        }
        Direction direction = reverseOrder ? DESC : ASC;
        return repository.findAll(specification, PageRequest.of(page - 1, 5, Sort.by(direction, "createdAt")));
    }

    public Claim acceptClaim(Long claimId) {
        Claim claim = getSentClaimById(claimId);
        if (claim != null) return changeClaimStatus(claim, ACCEPTED);
        else throw new ResourceNotFoundException(
                    String.format(
                            "The claim with identifier %s was not found or has a status other than sent", claimId));
    }

    public Claim declineClaim(Long claimId) {
        Claim claim = getSentClaimById(claimId);
        if (claim != null) return changeClaimStatus(claim, REJECTED);
        else throw new ResourceNotFoundException(
                    String.format(
                            "The claim with identifier %s was not found or has a status other than sent", claimId));
    }

    public Claim editClaim(Long id, String edit, HttpServletRequest request) {
        var claim = readClaimForUser(id, request);

        if (!claim.getStatus().equals(ClaimStatus.DRAFT)) {
            throw new ClaimStatusException("You can only edit claims that have DRAFT status");
        }

        claim.setDescription(edit);
        return repository.save(claim);
    }

    private Claim getSentClaimById(Long claimId) {
        Specification<Claim> specification = Specification.where(null);
        specification = specification.and(ClaimSpecification.statusSent());
        specification = specification.and(ClaimSpecification.claimIdEqual(claimId));
        List<Claim> claims = repository.findAll(specification);
        if (!claims.isEmpty()) return claims.get(0);
        else throw new ResourceNotFoundException(
                String.format(
                        "The claim with identifier %s was not found or has a status other than sent", claimId));
    }

    private Claim changeClaimStatus(Claim claim, ClaimStatus status) {
        claim.setStatus(status);
        return repository.save(claim);
    }

}
