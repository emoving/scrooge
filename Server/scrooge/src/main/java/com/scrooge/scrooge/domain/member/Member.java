package com.scrooge.scrooge.domain.member;

import com.scrooge.scrooge.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @Column(length = 20, nullable = false)
    private String name;

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

    @Column
    private int exp;

    @Column
    private int streak;

    @Column(name = "weekly_goal")
    private int weeklyGoal;

    @Column(name = "weekly_consum")
    private int weeklyConsum;
    // int -> Integer 로 변경하는게 좋을 것 같다.

    @CreatedDate
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    /* 연결 */

    // 사용자가 소유한 아바타 목록
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberOwningAvatar> memberOwningAvatars = new ArrayList<>();

    // 소비 내역
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberOwningBadge> memberOwningBadgesOwningBadges = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberSelectedQuest> memberSelectedQuests = new ArrayList<>();

}