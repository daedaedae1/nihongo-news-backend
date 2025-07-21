package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // JSON, XML 등과 같은 데이터 반환 목적
@RequestMapping("/api")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> user(@RequestBody User user) {
        String checkPassword = user.getPassword();

        User checkUser = userService.isUserExists(user);

        if (checkUser != null) {
            if (checkUser.getPassword().equals(checkPassword)) {
                return ResponseEntity.ok().body(Map.of("success", "로그인 성공!"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "비밀번호가 일치하지 않습니다!"));
            }
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "아이디가 일치하지 않습니다!"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){
        User check = userService.isUserExists(user);
        if (check == null) {
            userService.addUser(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("success", "회원가입 완료"));
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "이미 존재하는 아이디입니다"));
        }
    }


}
