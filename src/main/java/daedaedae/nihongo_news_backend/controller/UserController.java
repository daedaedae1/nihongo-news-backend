package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // JSON, XML 등과 같은 데이터 반환 목적
@RequestMapping("/api")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<?> user(@RequestBody User user) {
        System.out.println("userid:" + user.getUserid());
        System.out.println("password: " + user.getPassword());
        return ResponseEntity.ok().build();
    }

}
