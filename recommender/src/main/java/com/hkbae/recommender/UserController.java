package com.hkbae.recommender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("")
    public User getUser(@RequestParam("id") String id) {
        return userMapper.getUser(id);
    }

    @GetMapping("/exist")
    public int getUserCount(@RequestParam("id") String id) {
        return userMapper.getUserCount(id);
    }

    @GetMapping("/login")
    public User login(@RequestParam("id") String id, @RequestParam("password") String password) {
        return userMapper.login(id, password);
    }

    @PostMapping("")
    public int postUser(@RequestBody User user) {
        return userMapper.insertUser(user);
    }

    @PutMapping("/password")
    public int putUserPassword(@RequestParam("id") String id, @RequestParam("password") String password) {
        return userMapper.updateUserPassword(id, password);
    }

    @PutMapping("/userInfo")
    public int putUserInfo(@RequestParam("id") String id, @RequestParam("name") String name,
            @RequestParam("born") String born) {
        return userMapper.updateUserInfo(id, name, born);
    }

    @DeleteMapping("")
    public int deleteUser(@RequestParam("id") String id) {
        return userMapper.deleteUser(id);
    }

}
