package com.example.music.handler;

import java.sql.SQLIntegrityConstraintViolationException;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * @param e: DataIntegrityViolationException
	 * @return ResponseEntity 409 Conflict
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		log.error("데이터 무결성 위반: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 데이터입니다.");
	}

	/**
	 * @param e: SQLIntegrityConstraintViolationException
	 * @return ResponseEntity 409 Conflict
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<?> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
		log.error("SQL 무결성 제약 조건 위반: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body("제약 조건 위반: 중복된 값이 존재합니다.");
	}

	/**
	 * @param e: IllegalArgumentException
	 * @return ResponseEntity 400 Bad Request
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error("잘못된 인자: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
	}

	/**
	 * @param e: NullPointerException
	 * @return ResponseEntity 500 Internal Server Error
	 */
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
		log.error("Null Pointer Exception: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
	}

	/**
	 * @param e: MyBatisSystemException
	 * @return ResponseEntity 500 Internal Server Error
	 */
	@ExceptionHandler(MyBatisSystemException.class)
	public ResponseEntity<?> handleMyBatisSystemException(MyBatisSystemException e) {
		log.error("MyBatis System Exception: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
	}

	/**
	 * @param e: Exception
	 * @return ResponseEntity 500 Internal Server Error
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception e) {
		log.error("처리되지 않은 예외: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
	}
}
