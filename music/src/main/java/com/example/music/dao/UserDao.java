package com.example.music.dao;

import java.util.List;

import com.example.music.model.User;

public interface UserDao {
	void addUser(User user);
	List<User> getAllUsers();
	User getUserByUserId(String userId);
	void updateUser(User user);
	void deleteUser(String userId);
}
