package com.korotkov.exchange.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reported_users")
@Data
@NoArgsConstructor
public class ReportedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Column(name = "complaint_reason")
    private String complaintReason;

    @Column(name = "is_rejected")
    private Boolean isRejected;
}
