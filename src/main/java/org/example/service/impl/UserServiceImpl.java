package org.example.service.impl;

import org.example.pojo.User;

public interface UserServiceImpl {
    User findByUsername(String username);

    void register(String username,String password);

    void update(User user);

    void updateAvatar(String avatarUrl);

    void UpdatePwd(String password);
}
