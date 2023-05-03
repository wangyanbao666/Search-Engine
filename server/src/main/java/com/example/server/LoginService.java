package com.example.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LoginService {
    private final Repository repository;
    @Autowired
    LoginService (Repository repository){
        this.repository = repository;
    }

    public boolean handleRegister(String username, String password) throws IOException {
        return repository.createUser(username, password);
    }

    public boolean handleLogin(String username, String password) throws IOException {
        return repository.checkPassword(username, password);
    }

}
