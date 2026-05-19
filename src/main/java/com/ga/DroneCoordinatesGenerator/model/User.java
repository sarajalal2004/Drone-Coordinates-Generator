package com.ga.DroneCoordinatesGenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@ToString(exclude = {"password", "userProfile"})
public class User {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public enum Role{
        OWNER,
        RENTER,
        ADMIN
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Status{
        ACTIVE,
        INACTIVE
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private Boolean verified;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
