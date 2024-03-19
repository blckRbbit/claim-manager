package com.github.blckrbbit.claimmanager.repository.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.blckrbbit.claimmanager.util.component.ClaimStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Table
@Entity(name = "claims")
@NoArgsConstructor
public class Claim implements Comparable<Claim>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_from", nullable = false)
    private User userFrom;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Override
    public int compareTo(Claim that) {
        if (this.createdAt.isBefore(that.createdAt)) return -1;
        else if (this.createdAt.isAfter(that.createdAt)) return 1;
        else return 0;
    }

    @Override
    public String toString() {
        return String.format("{id=%s, user=%s, created=%s, text=%s, status=%s}",
                id, userFrom.getLogin(), createdAt, description, status);
    }
}
