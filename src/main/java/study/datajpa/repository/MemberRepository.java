package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//JpaspecificationExecutor 은 실무에서 안쓰는게 좋음. 지옥을 맛보게 됨
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor {


    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);
    List<Member> findByUsernameAndAgeLessThan(String username,int age);


   // @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    List<Member> findTop3HelloBy();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username ,@Param("age")int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "select m from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age")int age);


    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();


    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username")String username);

    //조회에서 많은 트래픽을 잡아먹는것은 복잡한 쿼리에서 나오니 이런 최적화는 비중이 크지않다
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


    <T> List<T> findProjectionsByUsername(@Param("username") String username,Class<T> type);

    @Query(value = "select * from member where username = ?",nativeQuery = true)
    Member findByNativeQuery(String username);


    @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t" ,
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);



}
