package com.example.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

@RestController
public class Controller {
    @Autowired
    private Service service;

    @RequestMapping(value = "/api/results", method = RequestMethod.POST)
    @ResponseBody
    public ArrayList getResult(@RequestParam String query) throws IOException {

        ArrayList result = service.getResult(query);
        return result;
    }
}
