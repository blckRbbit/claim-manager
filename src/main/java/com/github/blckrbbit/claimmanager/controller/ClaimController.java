package com.github.blckrbbit.claimmanager.controller;

import com.github.blckrbbit.claimmanager.service.ClaimService;
import com.github.blckrbbit.claimmanager.util.component.ResponseWrapper;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.CredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/claims")
public class ClaimController {
    private final ResponseWrapper responseWrapper;
    private final ClaimService service;

    @Transactional
    @PostMapping("/draft")
    public ResponseEntity<?> createDraftClaim(@RequestBody String claim, HttpServletRequest request) {
        return responseWrapper.response(service.createDraftClaim(claim, request), request, new CredentialsException());
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> sendClaim(@PathVariable Long id, HttpServletRequest request) {
        return responseWrapper.response(service.sendClaim(id, request), request, new CredentialsException());
    }

    @GetMapping
    public ResponseEntity<?> readAllClaimsByOwner(HttpServletRequest request,
                                                  @RequestParam(required = false, defaultValue = "1") int page,
                                                  @RequestParam(required = false, defaultValue = "false")
                                                              boolean reversed) {
        if (page < 1) page = 1;
        return responseWrapper.response(
                service.getClaimsPageForUser(request, reversed, page), request, new CredentialsException());
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> editClaim(@PathVariable Long id, @RequestBody String edit,  HttpServletRequest request) {
        return responseWrapper.response(service.editClaim(id, edit, request), request, new CredentialsException());
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getSentClaims(HttpServletRequest request,
                                           @RequestParam(required = false) String login,
                                           @RequestParam(required = false) Long id,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "false")
                                                       boolean reversed) {
        return responseWrapper.response(
                service.getClaimsPageForOperator(login, id, reversed, page), request, new CredentialsException());
    }

    @Transactional
    @PatchMapping("/accept/{id}")
    public ResponseEntity<?> acceptClaim(HttpServletRequest request, @PathVariable Long id) {
        return responseWrapper.response(service.acceptClaim(id), request, new CredentialsException());
    }

    @Transactional
    @PatchMapping("/decline/{id}")
    public ResponseEntity<?> declineClaim(HttpServletRequest request, @PathVariable Long id) {
        return responseWrapper.response(service.declineClaim(id), request, new CredentialsException());
    }


    @GetMapping("/processing")
    public ResponseEntity<?> readAllProcessedClaims(HttpServletRequest request,
                                                  @RequestParam(required = false) String login,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false, defaultValue = "1") int page,
                                                  @RequestParam(required = false, defaultValue = "false")
                                                          boolean reversed) {
        if (page < 1) page = 1;
        return responseWrapper.response(
                service.readAllProcessedClaims(request, login, status, reversed, page), request, new CredentialsException());
    }

}
