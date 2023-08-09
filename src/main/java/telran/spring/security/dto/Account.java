package telran.spring.security.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
@Data
public class Account{
	final String username;
	final String password;
	LocalDateTime expDate;
	final String[] roles;
	
}
