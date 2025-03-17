package com.example.demo.controller;

import com.example.demo.service.BankNameClassifierService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private BankNameClassifierService bankNameClassifierService;

    @GetMapping
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/test")
    @SneakyThrows
    public String test(@RequestParam String text) {
        return bankNameClassifierService.classify(text);
    }

    @PostMapping("/train-more")
    @SneakyThrows
    public void trainMore(@RequestParam String text,
                          @RequestParam String classValue) {
        bankNameClassifierService.trainMore(text, classValue);
    }

    @PostMapping("/remove-train")
    @SneakyThrows
    public void removeTrainingData(@RequestParam String text,
                                   @RequestParam String classValue) {
        bankNameClassifierService.removeTrainingData(text, classValue);
    }
}
