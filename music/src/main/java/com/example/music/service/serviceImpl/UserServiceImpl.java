package com.example.music.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.dao.UserDao;
import com.example.music.model.User;
import com.example.music.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserDao userDao;

	@Override
	public void addUser(User user) {
		userDao.addUser(user);
	}
	
	@Override
	public User getUserByUserId(String userId) {
        User user = userDao.getUserByUserId(userId);
		return user;
	}

	@Override
	public void updateUser(User user) {
		userDao.updateUser(user);
		
	}

	@Override
	public void deleteUser(String userId) {
		userDao.deleteUser(userId);
	}

	@Override
	public boolean login(String userId, String password) {
		User user = userDao.getUserByUserId(userId);
		if (user != null) {
			if (user.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAdmin(String userId) {
		User user = userDao.getUserByUserId(userId);
		if (user != null) {
			return user.getRole().equals("admin");
		}
		return false;
	}

	@Override
	public List<User> getAllUsers() {
		List<User> userList = userDao.getAllUsers();
		return userList;
	}

}
