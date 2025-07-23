package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // JSON, XML 등과 같은 데이터 반환 목적
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){
        User checkUser = userService.isUserExists(user);
        if (checkUser == null) {
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

    @PostMapping("/login")
    public ResponseEntity<?> user(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) {
        User checkUser = userService.isUserExists(user);

        if (checkUser != null) {
            if (passwordEncoder.matches(user.getPassword(), checkUser.getPassword())) {

                // 세션 생성
                HttpSession session = request.getSession();
                session.setAttribute("loginMember", checkUser);

                return ResponseEntity.ok().body(Map.of("success", "로그인 성공!",
                        "nickname", checkUser.getNickname()));
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


    @PostMapping("/dare")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        return ResponseEntity.ok(Map.of("userid", user.getUserid(),
                "nickname", user.getNickname(),
                "name", user.getName()));
    }

}
