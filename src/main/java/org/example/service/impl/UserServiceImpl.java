package org.example.service.impl;

import org.example.pojo.User;

public interface UserServiceImpl {
    User findByUsernamme(String username);

    void register(String username,String password);
}
