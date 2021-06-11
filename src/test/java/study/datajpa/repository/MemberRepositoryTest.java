package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {


    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    void testMember() {

        //given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());


        //then


        assertThat(findMember.get().getId()).isEqualTo(member.getId());
        assertThat(findMember.get()).isEqualTo(savedMember);


    }

    @Test
    void basicCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all= memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);


        assertThat(memberRepository.count()).isEqualTo(0);


    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result=memberRepository.findByUsernameAndAgeGreaterThan("AAA",15);

        List<Member> result2=memberRepository.findByUsernameAndAgeLessThan("BBB",40);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

        assertThat(result2.get(0).getUsername()).isEqualTo("BBB");
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0).getAge()).isEqualTo(30);
    }

    @Test
    public void findHelloBy(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> helloBy = memberRepository.findTop3HelloBy();
        for (Member member : helloBy) {
            System.out.println("member = " + member.getUsername());
        }
    }


    @Test
    public void testNamedQuery(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result  = memberRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");


    }
    @Test
    public void testQuery(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.get(0)).isEqualTo("AAA");


    }

    @Test
    public void findMemberDto(){
        Member m1=new Member("AAA",20);


        Team team=new Team("teamA");

        teamRepository.save(team);

        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }




    }


}
