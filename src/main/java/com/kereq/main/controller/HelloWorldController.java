package com.kereq.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @RequestMapping("/")
    public String helloWorld() {
        return "Hello Worldasdsaeresrser";
    }

    @RequestMapping("/second")
    public String second() {
        return "Second";
    }
}
