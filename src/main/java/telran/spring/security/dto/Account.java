package telran.spring.security.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class Account implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Size(min=5, message="username must be not less than 5 letters")
	final String username;
	@Size(min=8, message="password must be not less than 8 letters")
	final String password;
	LocalDateTime expDate;
	@NotEmpty
	final String[] roles;
	LinkedList<String> passwords;
	
}
