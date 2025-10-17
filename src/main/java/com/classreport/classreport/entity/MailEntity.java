package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mails")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mailFrom;
    @ElementCollection
    @CollectionTable(name = "mail_entity_mail_to", joinColumns = @JoinColumn(name = "mail_entity_id"))
    private List<String> mailTo;
    private String subject;
    private String text;

    @CreationTimestamp
    private LocalDateTime sendAt;

}
