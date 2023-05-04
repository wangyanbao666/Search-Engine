package com.example.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Hashtable;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping("/login")
    @ResponseBody
    public Hashtable login(@RequestParam String username, @RequestParam String password) throws IOException {
//        todo: service handle login
        boolean success = loginService.handleLogin(username, password);
        Hashtable userInfo = new Hashtable();
        userInfo.put("success", success);
        Hashtable history = loginService.fetchUserHistory(username);
        System.out.println(history.toString());
        userInfo.put("history", history);
        return userInfo;

    }
    @RequestMapping("/register")
    @ResponseBody
    public boolean register(@RequestParam String username, @RequestParam String password) throws IOException {
//        todo: service handle register
        return loginService.handleRegister(username, password);
    }


}
