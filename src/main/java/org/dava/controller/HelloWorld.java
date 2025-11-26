package org.dava.controller;

import lombok.AllArgsConstructor;
import org.dava.dao.HelloRepository;
import org.dava.domain.HelloEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class HelloWorld {

    @Autowired
    private final HelloRepository helloRepository;

    @GetMapping("/hello")
    public List<HelloEntity> hello() {
        return helloRepository.findAll();
    }
}
