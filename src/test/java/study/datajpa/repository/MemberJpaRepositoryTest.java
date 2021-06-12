package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {

        //given
        Member member = new Member("memberA");
        memberJpaRepository.save(member);

        //when
        Member member1 = memberJpaRepository.find(member.getId());



        //then

        assertThat(member1.getId()).isEqualTo(member.getId());
        assertThat(member1).isEqualTo(member);


    }

    @Test
    void basicCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all= memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);


        assertThat(memberJpaRepository.count()).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result=memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA",15);


        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1=new Member("AAA",20);
        Member m2=new Member("BBB",30);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result  = memberJpaRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");


    }

    @Test
    public void paging(){
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));


        PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));


        int age=10;
        int offset=1;
        int limit=3;

        //when

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount= memberJpaRepository.totalCount(age);

        //페이지 계산 공식 적용..
        //totalpage= totalcount/ size
        // 마지막 페이지
        // 최초 페이지..

        //then

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);


    }

    @Test
    public void bulkUpdate(){

        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));


        //when

        int resultCount=memberJpaRepository.bulkAgePlus(20);

        //then

        assertThat(resultCount).isEqualTo(3);


    }
}