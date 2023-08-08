package com.scrooge.scrooge.domain.member;

import com.scrooge.scrooge.domain.*;
import com.scrooge.scrooge.domain.community.Article;
import com.scrooge.scrooge.domain.community.ArticleComment;
import com.scrooge.scrooge.domain.community.ArticleGood;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "member.withRelatedEntities", attributeNodes = {
        @NamedAttributeNode("mainAvatar"),
        @NamedAttributeNode("mainBadge"),
        @NamedAttributeNode("level")
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_avatar_id")
    private Avatar mainAvatar;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_badge_id")
    private Badge mainBadge;

    @Column(columnDefinition = "int default 0")
    private Integer exp;

    @Column(columnDefinition = "int default 0")
    private Integer streak;

    @Column(name = "weekly_goal", columnDefinition = "int default 0")
    private Integer weeklyGoal;

    @Column(name = "weekly_consum", columnDefinition = "int default 0")
    private Integer weeklyConsum;
    // int -> Integer 로 변경하는게 좋을 것 같다.

    @CreatedDate
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    /* 연결 */
    // 소비 내역
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MemberOwningAvatar> memberOwningAvatars = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MemberOwningBadge> memberOwningBadgesOwningBadges = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MemberSelectedQuest> memberSelectedQuests = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Article> articles = new ArrayList<>();
}
