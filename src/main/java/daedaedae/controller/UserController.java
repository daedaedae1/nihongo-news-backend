package daedaedae.controller;

import daedaedae.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<?> user(@RequestBody User user) {
        System.out.println("userid:" + user.getUserid());
        System.out.println("password: " + user.getPassword());
        return ResponseEntity.ok().build();
    }

}
