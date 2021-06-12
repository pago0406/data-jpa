package study.datajpa.repository;

import org.hibernate.annotations.BatchSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {


    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberQueryRepository memberQueryRepository;
    @PersistenceContext
    EntityManager em;

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

    @Test
    public void findByNames(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Team team=new Team("teamA");

        teamRepository.save(team);


        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }


    }

    @Test
    public void returnType(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("AAA",30);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Team team=new Team("teamA");

        teamRepository.save(team);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + findMember);


    }
    @Test
    public void paging(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age=10;
        int offset=1;
        int limit=3;

        //when

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));



        //then

        List<Member> content = page.getContent();
        long totalElements= page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();


        for (Member member : page) {
            System.out.println("member = " + member);
        }

        //System.out.println("totalElements = " + totalElements);

        //페이지 계산 공식 적용..
        //totalpage= totalcount/ size
        // 마지막 페이지
        // 최초 페이지..

        //then




    }

    @Test
    public void bulkUpdate(){

        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));


        //when

        int resultCount=memberRepository.bulkAgePlus(20);
       // em.clear();

        List<Member> result = memberRepository.findByUsername("member5");





        Member member5 = result.get(0);
        System.out.println("member5 = " + member5.getAge());

        //then

        assertThat(resultCount).isEqualTo(3);


    }

    @Test
    public void findMemberLazy(){
        //given
        //member1-> teamA
        //member2-> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);


        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> all = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");
        System.out.println("findMember = " + findMember);


        em.flush();
    }


    @Test
    public void lock(){

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");



        em.flush();
    }

    @Test
    public void callCustom(){
        List<Member> members=memberRepository.findMemberCustom();
        List<Member> members1 = memberQueryRepository.finAllMembers();
    }

}
