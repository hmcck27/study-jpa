package jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // 이걸 만드는 순간 데이터베이스랑 연결이 됬다고 보면 된다.
        // application loading 시점에 딱 하나만 만들어야 한다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 트랜잭션 단위에서 항상 늘 만들어야 함.
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // 여기에 데이터 베이스 접근 코드를 작성하면 된다.
        // 데이터 베이스 접근 코드는 항상 트랜잭션 내에서 작성해야 한다.

        try  {

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("teamC");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.changeTeam(teamA);
            member1.setMemberType(MemberType.Admin);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(10);
            member2.changeTeam(teamA);
            member2.setMemberType(MemberType.Admin);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(10);
            member3.changeTeam(teamB);
            member3.setMemberType(MemberType.Admin);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("member4");
            member4.setAge(10);
            member4.setMemberType(MemberType.Admin);
            em.persist(member4);

            em.flush();
            em.clear();

            /**
             * 팀의 멤버를 갖고 오기
             */

            List<Member> members1 = em.createQuery("select t.members from Team t where t.id = :teamId")
                    .setParameter("teamId", teamA.getId())
                    .getResultList();

            System.out.println("members1 = " + members1);

            List<Member> members2 = em.createQuery("select m from Member m join m.team t on t.id = :teamId")
                    .setParameter("teamId", teamA.getId())
                    .getResultList();

            System.out.println("members2 = " + members2);


            String query = "select m from Member m join m.team on m.team.id = :teamId";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .setParameter("teamId", teamA.getId())
                    .getResultList();

            System.out.println("resultList = " + resultList);

//            System.out.println("members = " + members);


            /**
             * 조인없이 팀만 조회하기
             * 당연히 팀은 3개가 다 나오고
             * 밑의 지연 로딩 쿼리들이 3번 나간다.
             */

//            String query = "select t from Team t";
//
//            List<Team> teams = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            System.out.println("teams = " + teams.size());
//            System.out.println("teams = " + teams);
//            System.out.println("============이 밑의 쿼리들은 지연 로딩 쿼리 입니다. ==============");
//
//            for (Team team : teams) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("member = " + member.getUsername());
//                }
//            }

            /**
             * 조인없이 멤버만 조회하기
             * 당연히 멤버는 4개가 다 나오고
             * 지연로딩 쿼리는 2번 나간다. 왜냐하면 멤버들이 속한 팀은 총 2개니까. 나머지 2번은 다 1차캐시에서
             * 불러오게 된다.
             */

//            String query = "select m from Member m";
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .getResultList();
//
//            System.out.println("resultList.size = " + resultList.size());
//
//            System.out.println("members = " + resultList);
//            System.out.println("============이 밑의 쿼리들은 지연 로딩 쿼리 입니다. ==============");
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername());
//                System.out.println("member = " + member.getTeam());
//            }

            /**
             * member 기준 team과의 inner join.
             * 쿼리는 당연히 Inner join으로 나갈거고,
             * size는 3이다.
             * 지연 로딩으로 나가는 쿼리는 2번. 나머지 1번은 1차 캐시에서.
             */

//            String query = "select m from Member m join m.team ";
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .getResultList();
//
//            System.out.println("resultList.size = " + resultList.size());
//
//            System.out.println("members = " + resultList);
//            System.out.println("============이 밑의 쿼리들은 지연 로딩 쿼리 입니다. ==============");
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername());
//                System.out.println("member = " + member.getTeam());
//            }

            /**
             * member 기준 team과의 left join
             * 쿼리는 당연히 outer left join으로 나갈거고,
             * size는 4이다.
             * 지연 로딩으로 나가는 쿼리는 당연히 2개 ! 나머지 2개는 하나는 1차 캐시. 하나는 객체 없어서 쿼리 안나갈듯.
             */

//            String query = "select m from Member m left join m.team";
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .getResultList();
//
//            System.out.println("resultList.size = " + resultList.size());
//
//            System.out.println("members = " + resultList);
//            System.out.println("============이 밑의 쿼리들은 지연 로딩 쿼리 입니다. ==============");
//
//            for (Member member : resultList) {
//                System.out.println("member.username = " + member.getUsername());
//                System.out.println("member.team = " + member.getTeam());
//            }

            /**
             * team 기준 member와 inner join. batch size 없이.
             * 이러면 쿼리는 이너 조인으로 나가기는 한다. fetch 가 없이 진행되니까
             * 이너 조인이니까 당연히 size는 3여야 하는데. 이게 프록시라서 그냥 체크 안하고 넘기는듯
             * 지연 로딩으로 멤버 부르는 쿼리는 2번 나간다.
             */

//            String query = "select t from Team t join t.members";
//
//            List<Team> teams = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            System.out.println("teams = " + teams.size());
//            System.out.println("teams = " + teams);
//            System.out.println("============이 밑의 쿼리들은 지연 로딩 쿼리 입니다. ==============");
//
//            for (Team team : teams) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("member = " + member.getUsername());
//                }
//            }
            //--------------------------

            /**
             * 조인 대상 필터링
             */

//            String query = "select m, t from Member m join m.team t on t.name='teamA'";
//            List<Object[]> resultList = em.createQuery(query)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Object[] objects : resultList) {
//                for (Object object : objects) {
//                    System.out.println("object = " + object);
//                }
//            }

            /**
             * 회원과 팀을 반환하는데 회원이 이름이 1이라고 팀의 이름이 B인 경우를 반환
             * on은 조인 대상을 조인 전에 거르는 필터링이고,
             * where는 조인 결과를 필터링
             *
             */

//            String query = "select m.username ,t from Member m inner join m.team t on t.name='teamB' where m.username='member1' or t.name='teamB'";
//
//            List<Object[]> resultList = em.createQuery(query)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Object[] objects : resultList) {
//                for (Object object : objects) {
//                    System.out.println("object = " + object);
//                }
//            }

            /**
             * fetch join member with team
             */
//            String query = "select m from Member m left join fetch m.team";
//
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//                System.out.println("member.team = " + member.getTeam());
//            }

            /**
             * fetch join team with member 일대다 조인이라서 데이터가 뻥튀기됨.
             */

//            String query = "select t from Team t join fetch t.members";
//
//            List<Team> resultList = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Team team : resultList) {
//                System.out.println("team = " + team);
//                System.out.println("team.getMembers() = " + team.getMembers());
//            }

            /**
             * distinct로 뻥튀기된 조인 결과 줄이
             */

//            String query = "select distinct t from Team t left join fetch t.members";
//
//            List<Team> resultList = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Team team : resultList) {
//                System.out.println("team = " + team);
//                System.out.println("team.getMembers() = " + team.getMembers());
//            }

            /**
             * 엔티티를 파라미터로 넘기기 기본 키 값
             */

//            String query = "select m from Member m where m = :mbr";
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .setParameter("mbr", member1)
//                    .getResultList();
//
//            System.out.println("resultList = " + resultList);
//            for (Member member : resultList) {
//
//                System.out.println("member.getUsername() = " + member.getUsername());
//                System.out.println("member.getTeam() = " + member.getTeam());
//            }

            /**
             * 엔티티를 파라미터로 넘기기 외래 키 값
             */

//            String query = "select m from Member m where m.team = :team";
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .setParameter("team", teamA)
//                    .getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

            /**
             * 네임드 쿼리 사용
             */

//            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
//                    .setParameter("username", "member1")
//                    .getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

            /**
             * 모든 회원의 나이를 20살로 바꾸기
             */
//            String query = "update Member m set m.age = 20";
//            int resultCount = em.createQuery(query)
//                    .executeUpdate();
//
//            System.out.println("resultCount = " + resultCount);
//
//            System.out.println("member1 = " + member1.getAge());
//            System.out.println("member2 = " + member2.getAge());
//            System.out.println("member3 = " + member3.getAge());
//            System.out.println("member4 = " + member4.getAge());

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            // entity manager를 닫아주는게 중요하다.
            // 이게 디비 트랜잭션을 물고 작동한다.
            em.close();
        }

        // 실제 application이 끝나면 이 팩토리를 닫아줘야 한다.
        emf.close();

    }


}
