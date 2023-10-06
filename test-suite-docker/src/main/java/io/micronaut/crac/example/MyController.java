package io.micronaut.crac.example;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class MyController {

    @Get
    public String index() {
        return "Hello World";
    }
}
