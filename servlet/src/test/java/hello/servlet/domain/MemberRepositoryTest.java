package hello.servlet.domain;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberRepositoryTest {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() { // 각 테스트가 끝날 때, 다음 테스트에 영향을 주지 안도록 초기화
        memberRepository.clearStore();
    }

    @Test
    void save() {
        // given 주어진 것을
        Member member = new Member("hello", 20);

        // when 실행 했을때
        Member saveMember = memberRepository.save(member);

        // then 결과
        Member findMember = memberRepository.findById(saveMember.getId());
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    void findAll() {
        // given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
    }
}
