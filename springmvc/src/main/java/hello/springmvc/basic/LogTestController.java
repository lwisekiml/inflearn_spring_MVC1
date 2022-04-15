package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
// @RestController는 반환 값으로 뷰를 찾는 것이 아닌 HTTP 메시지 바디에 바로 입력하여 실행 결과로 ok 메세지를 받을 수 있다.
// @Controller는 반환 값이 String 이면 뷰 이름으로 인식되어 뷰를 찾고 뷰가 랜더링 된다.
@RestController
public class LogTestController {

    private final Logger log = LoggerFactory.getLogger(getClass()); // @Slf4j 애노테이션과 같다.

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name=" + name);
        // 로그 출력 레벨을 info로 설정해도 + 연산이 실행되어 사용 x
        log.trace("info log=" + name);

        log.trace("trace log={}", name);
        log.debug("trace log={}", name);
        log.info("trace log={}", name);
        log.warn("trace log={}", name);
        log.error("trace log={}", name);

        return "ok";
    }
}
