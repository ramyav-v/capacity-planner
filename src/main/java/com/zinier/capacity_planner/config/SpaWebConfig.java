package com.zinier.capacity_planner.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaWebConfig {

    @RequestMapping(value = {
        "/{path:^(?!capacityplanner|api|actuator)[^\\.]*$}",
        "/{path:^(?!capacityplanner|api|actuator)[^\\.]*$}/**"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
