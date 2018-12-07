package com.common.web.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by alex.
 * Date: 2018-12-07
 */
@Controller
public class DefaultController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
