package telran.spring.security;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.spring.exceptions.NotFoundException;
import telran.spring.security.dto.Account;
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
	 final PasswordEncoder passwordEncoder;
     final AccountProvider provider;
     ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
     @Value("${app.expiration.period:600}")
     long expirationPeriodHours;
     @Value("${app.security.passwords.limit:3}")
		int limitPasswords;
     @Autowired
      UserDetailsManager userDetailsManager;
     @Value("${app.security.validation.period:3600000}")
	private long validationPeriod;
     
	@Override
	public Account getAccount(String username) {
		Account res = accounts.get(username);
		if (res == null) {
			throw new NotFoundException(username + " not found");
		}
		return res;
	}

	@Override
	public void addAccount(Account account) {
		String username = account.getUsername();
		if (userDetailsManager.userExists(username)) {
			throw new IllegalStateException(String.format("user %s already exists", username));
		}
		if(accounts.containsKey(username)) {
			throw new RuntimeException("error of synchronization between accounts and accounts manager");
		}
		
		String plainPassword = account.getPassword();
		String passwordHash = passwordEncoder.encode(plainPassword);
		Account user = new Account(username, passwordHash, account.getRoles());
		
		LocalDateTime ldt = LocalDateTime.now().plusHours(expirationPeriodHours);
		user.setExpDate(ldt);
		
		LinkedList<String> passwords = new LinkedList<>();
		passwords.add(passwordHash);
		user.setPasswords(passwords);
		
		
		createUser(user);
		log.debug("created user {}", username);
		
	}

	private void createUser(Account user) {
		accounts.putIfAbsent(user.getUsername(), user) ;
		userDetailsManager.createUser(User.withUsername(user.getUsername())
				.password(user.getPassword()).roles(user.getRoles())
				.accountExpired(LocalDateTime.now().compareTo(user.getExpDate())<=0)
				.build());
		
	}

	@Override
	public void updatePassword(String username, String newPassword) {
		Account account = accounts.get(username);
		if (account == null) {
			throw new NotFoundException(username + " not found");
		}
		updateUser(account, newPassword);
		log.debug("use {} updated", username);

	}

	private void updateUser(Account account, String newPassword) {
		if (account.getPasswords().stream()
				.anyMatch(hash -> passwordEncoder.matches(newPassword, hash))) {
			throw new IllegalStateException("mismatches Passwords Strategy");
		}
		LinkedList<String> passwords = account.getPasswords();
		String hashPassword = passwordEncoder.encode(newPassword);
		
		if (passwords.size() == limitPasswords) {
			passwords.removeFirst();
		}
		passwords.add(hashPassword);
		String username = account.getUsername();
		String[]roles = account.getRoles();
		Account newAccount = new Account(username, hashPassword, roles);
		newAccount.setPasswords(passwords);
		LocalDateTime expTime = LocalDateTime.now().plusHours(expirationPeriodHours);
		newAccount.setExpDate(expTime);
		accounts.put(username, newAccount);
		userDetailsManager.updateUser(User.withUsername(username)
				.password(hashPassword).roles(roles)
				.accountExpired(LocalDateTime.now()
						.compareTo(expTime) <= 0).build());
		
	}

	@Override
	public void deleteAccount(String username) {
		Account account = accounts.remove(username);
		if (account == null) {
			throw new NotFoundException(username + " not found");
		}
		userDetailsManager.deleteUser(username);
		log.debug("user {} deleted", username);

	}
	@PostConstruct
	void restoreAccounts() {
		Thread thread = new Thread(() -> {
			while(true) {
			
				try {
					Thread.sleep(validationPeriod);
				} catch (InterruptedException e) {
					
				}
				expirationValidation();
			}
		});
		thread.setDaemon(true);
		thread.start();
		List<Account> listAccounts = provider.getAccounts();
		listAccounts.forEach(a -> createUser(a));
	}
	private void expirationValidation() {
		log.debug("expiration validation");
		accounts.values().stream().filter(this::isExpired).forEach(a -> 
				userDetailsManager.updateUser(User.withUsername(a.getUsername())
						.accountExpired(true).build())
			
		);
		
	}

	private boolean isExpired(Account a) {
		
		return LocalDateTime.now().isBefore(a.getExpDate());
	}

	@PreDestroy
	void saveAccounts() {
		provider.setAccounts(new LinkedList<>(accounts.values()));
	}
	
	

}
