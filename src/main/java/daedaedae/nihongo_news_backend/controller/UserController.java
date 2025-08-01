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
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) {
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
                        .status(HttpStatus.BAD_REQUEST) // 잘못된 값을 의미
                        .body(Map.of("error", "비밀번호가 일치하지 않습니다!"));
            }
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "아이디가 일치하지 않습니다!"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response) {
        // 세션 무효화
        session.invalidate();

        // 프론트의 JSESSIONID 삭제
        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);

        return ResponseEntity.ok().build();
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

    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam("userid") String userid) {
        boolean result = userService.existsByUserid(userid);
        if (result) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)    // 비즈니스 로직의 실패를 의미, 409 Conflict
                    .body(Map.of("error", "이미 존재하는 아이디입니다."));
        }
        else {
            return ResponseEntity.ok(Map.of("success", "사용 가능한 아이디입니다."));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user, HttpSession session) {
        User updateUser = user;
        User loginUser = (User) session.getAttribute("loginMember");

        if (loginUser.getUserid().equals(updateUser.getUserid())) {
            userService.updateUser(updateUser);

            User latestUser = userService.isUserExists(updateUser);

            session.setAttribute("loginMember", latestUser);

            return ResponseEntity.ok(Map.of("success", "회원정보가 수정되었습니다.",
                    "user", latestUser));
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "회원정보 수정에 실패했습니다."));
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePwd(@RequestBody Map<String, String> request, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        userService.changePwd(user, request.get("newPassword"));
        session.setAttribute("loginMember", user);
        return ResponseEntity.ok(Map.of("success", "비밀번호가 변경되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpSession session, HttpServletResponse response) {
        User user = (User) session.getAttribute(("loginMember"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        userService.deleteUser(user);
        session.invalidate();
        // 프론트의 JSESSIONID 삭제
        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);

        boolean stillExists = userService.existsByUserid(user.getUserid());
        if (!stillExists) {
            return ResponseEntity.ok(Map.of("success", "회원 탈퇴 성공"));
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "회원 탈퇴에 에러 발생"));
        }
    }

}
