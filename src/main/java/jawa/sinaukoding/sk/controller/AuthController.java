package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.LoginReq;
import jawa.sinaukoding.sk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Response<Object> login(@RequestBody LoginReq req) {
        return userService.login(req);
    }
}
