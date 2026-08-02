package com.ivan.taskflowapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_members")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private User invitedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private ProjectMemberRole role;

}
