package org.example.service;

import lombok.val;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.example.service.impl.UserServiceImpl;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUsername(String username) {
        User u=userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username,String password) {
        String md5String = Md5Util.getMD5String(password);
        userMapper.add(username,md5String);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id=(Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);

    }

    @Override
    public void UpdatePwd(String password) {
        Map<String ,Object> map=ThreadLocalUtil.get();
        Integer id =(Integer) map.get("id");
        password =Md5Util.getMD5String(password);
        userMapper.updatePwd(id,password);
    }
}
