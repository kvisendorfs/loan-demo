package lv.kvisendorfs.loandemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LoanDemoApplication implements CommandLineRunner {

	private final UserInputService userInputService;

	public static void main(String[] args) {
		SpringApplication.run(LoanDemoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		userInputService.comunicateWithUser();
	}

}
