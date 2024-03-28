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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.sax.SAXResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
   private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


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
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,100, TimeUnit.HOURS);
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
        Map<String,Object> map =ThreadLocalUtil.get();
        String token = (String) map.get("token");
        String oldPwd = passwords.get("old_pwd");
        String newPwd = passwords.get("new_pwd");
        String rePwd =  passwords.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码,再和old_pwd比对
        String username = (String) map.get("username");
        User loginUser = userService.findByUsername(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }

        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }
        ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();

        operations.getOperations().delete(token);
        userService.UpdatePwd(newPwd);
        return Result.success();
    }

    @GetMapping("test")
    public  Result test(){
        try {
            // 指定 Python 解释器和 Python 脚本路径
            String pythonInterpreter = "python";
            String pythonScript = "C:\\Users\\wyl\\PycharmProjects\\pythonProject\\test2.py";

            // 构建进程
            ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreter, pythonScript);

            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输出流
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // 读取输出
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程执行结束
            int exitCode = process.waitFor();
            System.out.println("Python script execution finished with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return Result.success();
    }



}
