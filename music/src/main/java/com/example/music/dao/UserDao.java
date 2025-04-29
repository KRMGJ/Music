package com.example.music.dao;

import java.util.List;

import com.example.music.model.User;

public interface UserDao {
	void addUser(User user);
	void addSocialUser(User user);
	List<User> getAllUsers();
	User getUserByEmail(String email);
	void updateUser(User user);
	void deleteUser(String email);
}
