package travel.travel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReactController {

    @GetMapping("/api/hello")
    public String hello() {
        return "테스트입니다.";
    }
}
