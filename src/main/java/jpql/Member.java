package jpql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.lang.annotation.Target;

@Entity
@Getter
@Setter
@ToString(exclude = {"team"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m .username= :username"
)
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "age")
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(value = EnumType.STRING)
    private MemberType memberType;

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

}
