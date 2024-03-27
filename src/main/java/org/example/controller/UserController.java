package org.example.controller;


import com.github.pagehelper.Page;
import jakarta.validation.constraints.Pattern;
import lombok.val;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.sax.SAXResult;
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

       User u= userService.findByUsername(username);
       if(u==null){
           userService.register(username,password);

           return Result.success();
       }else {
           return Result.error("用户名已被占用");
       }

    }

    @PostMapping ("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String  username,@Pattern(regexp = "^\\S{5,16}$")String password){
        User u =userService.findByUsername(username);
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

    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name="Authorization") String token*/){

//        Map<String,Object> map = JwtUtil.parseToken(token);
//        String username =(String) map.get("username");
        Map<String ,Object> map= ThreadLocalUtil.get();
        String username=(String) map.get("username");
        User user =userService.findByUsername(username);
        return Result.success(user);
    }

    @PutMapping("update")
    public  Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("updateAvatar")
    public  Result updateAvatar(@RequestParam  String avatarUrl){

        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("updatePwd")
    public Result updatePwd(@RequestBody  Map<String,String> passwords){
        String oldPwd = passwords.get("old_pwd");
        String newPwd = passwords.get("new_pwd");
        String rePwd =  passwords.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码,再和old_pwd比对
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUsername(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }

        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }

        userService.UpdatePwd(newPwd);
        return Result.success();
    }



}
