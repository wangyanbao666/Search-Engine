package com.example.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @RequestMapping("/login")
    @ResponseBody
    public boolean login(@RequestParam String username, @RequestParam String password) throws IOException {
//        todo: service handle login
        return loginService.handleLogin(username, password);

    }
    @RequestMapping("/register")
    @ResponseBody
    public boolean register(@RequestParam String username, @RequestParam String password) throws IOException {
//        todo: service handle register
        return loginService.handleRegister(username, password);
    }


}
