package com.example.server;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @RequestMapping(value = "/api/results", method = RequestMethod.POST)
    @ResponseBody
    public ArrayList getResult(@RequestParam String query, @RequestParam String username) throws IOException {
        System.out.println(username);
        ArrayList result = service.getResult(query);
        service.storeUserHistory(username, query, result);
        return result;
    }
}
