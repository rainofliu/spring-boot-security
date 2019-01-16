package com.tianba.springboot.springsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: ajin
 * @Date: 2019/1/16 19:32
 * @Description:后端入口
 */
@Controller
public class IndexController {

    @RequestMapping("/content")
    public String content(){
        return "content";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "login";
    }
    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }
}
