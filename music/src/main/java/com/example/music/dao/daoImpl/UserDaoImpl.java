package com.example.music.dao.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.UserDao;
import com.example.music.model.User;

@Repository
public class UserDaoImpl implements UserDao {
	
	@Autowired
	SqlSession sqlSession;

	@Override
	public void addUser(User user) {
		sqlSession.insert("user.addUser", user);
	}

	@Override
	public User getUserByUserId(String userId) {
        User user = sqlSession.selectOne("user.getUserByUserId", userId);
		return user;
	}

	@Override
	public void updateUser(User user) {
        sqlSession.update("user.updateUser", user);
	}

	@Override
	public void deleteUser(String userId) {
		sqlSession.delete("user.deleteUser", userId);
	}

	@Override
	public List<User> getAllUsers() {
		List<User> userList = sqlSession.selectList("user.getAllUsers");
		return userList;
	}

}
