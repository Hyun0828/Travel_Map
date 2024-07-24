package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.service.NaverSearchService;

@RestController
@RequestMapping("/naver")
@RequiredArgsConstructor
public class NaverSearchController {

    private final NaverSearchService naverSearchService;

    @GetMapping(value = "/search", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> naverSearchList(@RequestParam(name = "text") String text) {
        System.out.println(text);
        String responseBody = naverSearchService.search(text);
        return ResponseEntity.ok(responseBody);
    }
}
