package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
// final 이 붙은 멤버변수만 사용해서 생성자를 자동으로 만들어준다.
// 이렇게 생성자가 딱 1개만 있으면 스프링이 해당 생성자에 @Autowired로 의존관계를 주입해 준다.
// 따라서 final 키워드를 빼면 안된다.
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired // 생성자 하나만 있으면 생략가능
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    // 상품 등록 폼
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    // 상품 등록 처리
//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item); // item.html에서 {item.id} 중 item을 의미

        return "basic/item";
    }

    /**
     * @ModelAttribute("item") Item item
     * model.addAttribute("item", item); 자동 추가
     *
     * @ModelAttribute
     * - 요청 파라미터 처리 : Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXxx)으로 입력해준다.
     * - Model 추가 : Model에 @ModelAttribute로 지정한 객체를 자동으로 넣어준다.
     * (데이터를 담을 때 이름은 @ModelAttribute에 지정한 name(value / 여기서는 ("item")값) 속성을 사용한다.
     *
     * @ModelAttribute("hello") Item item --> 이름을 hello 로 지정
     * model.addAttribute("hello", item); --> 모델에 hello 이름으로 저장
     */
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item/*, Model model*/) {
        itemRepository.save(item);
//        model.addAttribute("item", item); // 자동 추가, 생략 가능
        return "basic/item";
    }

    /**
     * @ModelAttribute name 생략 가능
     * model.addAttribute(item); 자동 추가, 생략 가능
     * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록 Item -> item
     */
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * @ModelAttribute 자체 생략 가능
     * model.addAttribute(item) 자동 추가
     *
     * 상품 등록을 완료하고 웹 브라우저의 새로고침을 하면 상품이 중복 등록된다.
     * 이유는 웹 브라우저의 새로고침은 마지막에 서버에 전송한 데이터를 다시 전송하기 때문이다.
     */
//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * PRG - Post/Redirect/Get
     *
     * 상품 저장 후에 뷰 템플릿으로 이동하는 것이 아니라, 상품 상세 화면으로 리다이렉트를 호출
     * 웹 브라우저는 리다이렉트의 영향으로 상품 저장 후에 실제 상품 상세 화면으로 다시 이동한다.
     * 따라서 마지막에 호출한 상품 상세 화면인 GET /items/{id} 가 된다.
     *
     * <주의>
     * redirect에서 +item.getId() 처럼 URL에 변수를 더해서 사용하는 것은 URL 인코딩이 안되기 때문에 위험하다.
     * RedirectAttributes를 사용하자
     */
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();
    }

    /**
     * RedirectAttributes
     *
     * 실행하면 다음과 같은 리다이렉트 결과가 나온다.
     * http://localhost:8080/basic/items/3?status=true
     *
     * RedirectAttributes를 사용하면 URL 인코딩도 해주고, pathVarible, 쿼리 파라미터까지 처리해준다.
     *
     * redirect : /basic/items/{itemId}
     * pathVariable 바인딩 : {itemId}
     * 나머지는 쿼리 파라미터로 처리 : ?status=true
     */
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item saveItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", saveItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}"; // {itemId} 값은 "itemId" 값이다.
    }

    // 상품 수정 폼 컨트롤러
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    // 상품 수정 개발
    @PostMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; // 상세화면으로 이동, {itemId}값은 @PathVariable Long itemId 이다.
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct // 해당 빈의 의존관계가 모두 주입되고 나면 초기화 용도로 호출
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
