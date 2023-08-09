package telran.spring.security;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import telran.spring.security.dto.Account;
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	 final PasswordEncoder passwordEncoder;
     final AccountProvider provider;
     @Value("${app.expiration.period:600}")
     long expirationPeriodHours;
     @Value("${app.security.admin.password")
     String adminPassword;
     @Value("${app.security.admin.username:admin")
     String adminUsername;
     @Autowired
      UserDetailsManager userDetailsManager;
     @Bean
     UserDetailsManager getUserDetailsService() {
    	 UserDetailsManager manager = new InMemoryUserDetailsManager();
    	
    	
    	 manager.createUser(User.withUsername(adminUsername).password(passwordEncoder.encode(adminPassword)).roles("ADMIN").build());
    	 return manager;
     }
     //TODO
	@Override
	public Account getAccount(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAccount(Account account) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePassword(String username, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAccount(String username) {
		// TODO Auto-generated method stub

	}
	@PostConstruct
	void restoreAccounts() {
		//TODO
	}
	@PreDestroy
	void saveAccounts() {
		//TODO
	}
	@Scheduled(fixedDelay=1, timeUnit = TimeUnit.HOURS)
	void expirationValidation() {
		//TODO
	}
	

}
