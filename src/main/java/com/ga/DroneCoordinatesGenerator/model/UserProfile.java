package com.ga.DroneCoordinatesGenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_profile")
@ToString(exclude = "user")
public class UserProfile {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private Long phoneNumber;

    @Column
    private String address;

    @Column
    private Long cpr;

    @Column
    private LocalDate birthDate;

    @Column
    @JsonIgnore
    private String profileImageURL;

    @OneToOne(mappedBy = "userProfile", fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
