package com.example.music.service;

import java.util.List;

import com.example.music.model.User;

public interface UserService {
	void addUser(User user);
	void addSocialUser(User user);
	List<User> getAllUsers();
	User getUserByEmail(String email);
	void updateUser(User user);
	void deleteUser(String email);
	boolean login(String email, String password);
	boolean isAdmin(String email);
}
