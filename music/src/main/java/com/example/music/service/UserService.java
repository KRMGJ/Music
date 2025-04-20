package com.example.music.service;

import java.util.List;

import com.example.music.model.User;

public interface UserService {
	void addUser(User user);
	List<User> getAllUsers();
	User getUserByUserId(String userId);
	void updateUser(User user);
	void deleteUser(String userId);
	boolean login(String userId, String password);
	boolean isAdmin(String userId);
}
