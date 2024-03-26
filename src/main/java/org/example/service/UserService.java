package org.example.service;

import lombok.val;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.example.service.impl.UserServiceImpl;
import org.example.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUsernamme(String username) {
        User u=userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username,String password) {
        String md5String = Md5Util.getMD5String(password);
        userMapper.add(username,md5String);
    }
}
