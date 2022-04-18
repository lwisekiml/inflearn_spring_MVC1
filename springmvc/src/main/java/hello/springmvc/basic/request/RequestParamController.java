package hello.springmvc.basic.request;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok");
    }

    /** http://localhost:8080/request-param-v2?username=hello&age=20
     * @RequestParam 사용
     * - 파라미터 이름으로 바인딩
     * @ResponseBody 추가
     * - View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
     */
    // @Controller는 return이 String 경우 뷰 이름으로 인식하여 뷰를 찾고 뷰를 랜더링된다.
    // @ResponseBody를 사용하면 View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2(@RequestParam("username") String memberName,
                                 @RequestParam("age") int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }

    /** http://localhost:8080/request-param-v3?username=hello&age=20
     * @RequestParam 사용
     * HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
     */
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(@RequestParam String username, @RequestParam int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /** http://localhost:8080/request-param-v4?username=hello&age=20
     * @RequestParam 사용
     * String, int, Integer 등의 단순 타입이면 @RequestParam 도 생략 가능
     * @RequestParam 애노테이션을 생략하면 스프링 MVC는 내부에서 required=false 를 적용
     */
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(String username, int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * @RequestParam.required
     * /request-param -> username이 없으므로 예외
     *
     * 주의!
     * /request-param?username= -> 빈문자로 통과
     *
     * 주의!
     * /request-param
     * int age -> null을 int에 입력하는 것은 불가능, 따라서 Integer 변경해야 함(또는 다음에 나오는
    defaultValue 사용)
     */
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * @RequestParam
     * - defaultValue 사용
     *
     * 참고: defaultValue는 빈 문자의 경우에도 적용
     * /request-param?username=
     *
     * 이미 기본값이 있기 때문에 required는 의미가 없다.
     */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") String age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /** http://localhost:8080/request-param-map?username=kim&username=Lee
     * @RequestParam Map, MultiValueMap
     * Map(key=value)
     * MultiValueMap(key=[value1, value2, ...] ex) (key=userIds, value=[id1, id2])
     *
     * 파라미터의 값이 1개가 확실하다면 Map 을 사용해도 되지만, 그렇지 않다면 MultiValueMap 을 사용하자
     */
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
//    public String requestParamMap(@RequestParam MultiValueMap<String, Object> paramMap) { // username=[kim, Lee], age=null

        log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
        return "ok";
    }

    /** http://localhost:8080/model-attribute-v1?username=hello&age=20
     * @ModelAttribute 사용
     * 참고: model.addAttribute(helloData) 코드도 함께 자동 적용됨, 뒤에 model을 설명할 때 자세히 설명
     *
     * HelloData 객체 생성
     * 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티 찾고, 해당 프로퍼티의 setter를 호출하여 파라미터의 값을 입력(바인딩)
     * ex) 파라미터 이름이 username이면 setUsername() 메서드를 찾아 호출하면서 값을 입력
     *
     * 프로퍼티
     * 객체에 getUsername(), setUsername() 메서드가 있으면, 이 객체는 username이라는 프로퍼티를 가지고 있다.
     * username 프로퍼티 값을 변경하면 setUsername()이 호출되고, 조회하면 getUsername()이 호출
     *
     * http://localhost:8080/model-attribute-v1?username=hello&age=abc
     * 바인딩 오류
     * age=abc처럼 숫자가 들어가야 할 곳에 문자를 넣으면 BindException 발생
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {
//    public String modelAttributeV1(@RequestParam String username, @RequestParam int age) {
//        HelloData data = new HelloData();
//        data.setUsername(username);
//        data.setAge(age);

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        log.info("helloData={}", helloData); // helloData=HelloData(username=hello, age=20)
        return "ok";
    }

    /**
     * @ModelAttribute 생략 가능하며 생략시 다음과 같은 규칙을 적용
     * String, int 같은 단순 타입 = @RequestParam
     * argument resolver(HttpServletRequest 등) 로 지정해둔 타입 외 = @ModelAttribute
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(),
                helloData.getAge());
        return "ok";
    }
}
