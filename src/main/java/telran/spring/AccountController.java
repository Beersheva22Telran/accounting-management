package telran.spring;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import telran.spring.security.AccountService;
import telran.spring.security.PasswordValidator;
import telran.spring.security.dto.Account;
@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor

public class AccountController implements AccountService {
final AccountService accountService;
final PasswordValidator passwordValidator;
	@Override
	@GetMapping("{username}")
	public Account getAccount(@PathVariable String username) {
		
		return accountService.getAccount(username);
	}

	@Override
	@PostMapping
	public void addAccount(@RequestBody @Valid Account account) {
		passwordValidator.validate(account.getPassword());
		accountService.addAccount(account);

	}

	@Override
	@PutMapping("{username}")
	public void updatePassword(@PathVariable String username, @RequestBody String newPassword) {
		passwordValidator.validate(newPassword);
		accountService.updatePassword(username, newPassword);

	}

	@Override
	@DeleteMapping("{username}")
	public void deleteAccount(@PathVariable String username) {
		accountService.deleteAccount(username);

	}

}
