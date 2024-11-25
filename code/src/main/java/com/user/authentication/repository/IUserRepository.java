package com.user.authentication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.authentication.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

	/**
	 * @param email
	 * @param status
	 * @return
	 */
	Optional<User> findByUserEmailAndStatusNot(String email, String status);

	/**
	 * @param email
	 * @param token
	 * @param status
	 * @return
	 */
	Optional<User> findByUserEmailAndResetPasswordTokenAndStatusNot(String email, String token, String status);

	Optional<List<User>> findByStatusNotAndIsEmailSentFalse(String isDeleted);

	Optional<User> findByUserEmailAndStatusNotAndIsEmailSentFalse(String string, String isDeleted);

	Optional<User> findByUserEmailAndStatus(String userEmail, String isActive);

	Optional<User> findByUserIdAndStatus(String userId, String isActive);

	Optional<User> findByUserIdAndStatusNot(String userName, String isDeleted);

}
