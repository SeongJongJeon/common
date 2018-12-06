package com.common.web.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Api(tags = {"A. Test default"}, description = "For test")
@Controller
public class DefaultController {
    @ApiIgnore
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String index() {
        return "redirect:/swagger-ui.html";
    }

    @ApiOperation(value = "Check infomation of client")
    @RequestMapping(value = "/checkClientInfo", method = {RequestMethod.GET})
    @ResponseBody
    public String checkClientInfo(HttpServletRequest req, HttpServletResponse res) {

        return "success";
    }
}
