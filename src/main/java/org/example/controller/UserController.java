package org.example.controller;


import jakarta.validation.constraints.Pattern;
import lombok.val;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
   private UserService userService;


    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$")String password){

       User u= userService.findByUsernamme(username);
       if(u==null){
           userService.register(username,password);

           return Result.success();
       }else {
           return Result.error("用户名已被占用");
       }

    }

    @PostMapping ("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String  username,@Pattern(regexp = "^\\S{5,16}$")String password){
        User u =userService.findByUsernamme(username);
        if(u==null){
            return  Result.error("用户不存在");
        }
        if(Md5Util.getMD5String(password).equals(u.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",u.getId());
            claims.put("username",u.getUsername());
            val token = JwtUtil.getToken(claims);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }






}
