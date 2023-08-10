package telran.spring.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.TestPropertySource;
@SpringBootTest(classes= {AccountProviderImpl.class,
		AccountServiceImpl.class, AccountingConfiguration.class})
@TestPropertySource(properties = {"app.security.admin.password=ppp"})
class AccountingServiceTest {
	@Autowired
UserDetailsManager userDetailsManager;
	@Test
	void adminExtsTest() {
		
		assertTrue(userDetailsManager.userExists("admin"));
	}

}
