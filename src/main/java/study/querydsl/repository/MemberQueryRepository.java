package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MemberQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition),
                        teamNameEq(condition),
                        ageGoe(condition),
                        ageLoe(condition)
                )
                .fetch();
    }

    private BooleanExpression usernameEq(MemberSearchCondition condition) {
        return hasText(condition.getUsername()) ? member.username.eq(condition.getUsername()) : null;
    }

    private BooleanExpression teamNameEq(MemberSearchCondition condition) {
        return hasText(condition.getTeamName()) ? team.name.eq(condition.getTeamName()) : null;
    }

    private BooleanExpression ageGoe(MemberSearchCondition condition) {
        return condition.getAgeGoe() != null ? member.age.goe(condition.getAgeGoe()) : null;
    }

    private BooleanExpression ageLoe(MemberSearchCondition condition) {
        return condition.getAgeLoe() != null ? member.age.loe(condition.getAgeLoe()) : null;
    }
}
