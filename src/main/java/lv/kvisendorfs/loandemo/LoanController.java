package lv.kvisendorfs.loandemo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

	private final FixedInterestLoanCalculator loanCalculator;

	@GetMapping("/calculator")
	public List<MonthlyPayment> calculatePaymentPlan(@RequestParam BigDecimal loanAmount, @RequestParam int loanTermInYears) {
		return loanCalculator.calculatePaymentPlan(LoanType.HOUSING, loanAmount, loanTermInYears);
	}

}
