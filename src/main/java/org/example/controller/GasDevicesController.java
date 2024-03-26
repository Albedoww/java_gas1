package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.example.pojo.Result;
import org.example.utils.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gasdevices")
public class GasDevicesController {

    @GetMapping("/list")
    public Result<String> list(@RequestHeader(name="Authorization") String token, HttpServletResponse response){
        return Result.success("");

    }
}
