package jpql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto2 {

    private String username;
    private String hello;
    private Boolean Boolean;

    public MemberDto2(String username, String hello, java.lang.Boolean aBoolean) {
        this.username = username;
        this.hello = hello;
        Boolean = aBoolean;
    }

    public MemberDto2(java.lang.Boolean aBoolean) {
        Boolean = aBoolean;
    }
}
