package lv.kvisendorfs.loandemo;

import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.jakewharton.fliptables.FlipTableConverters;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInputService {

	private final FixedInterestLoanCalculator loanCalculator;

	public void comunicateWithUser() {
		System.out.println("");
		System.out.println("Loan Demo Application");
		System.out.println("");

		Scanner in = new Scanner(System.in);
		while (true) {

			System.out.println("Input loan amount:");
			var loanAmount = in.nextBigDecimal();

			System.out.println("Input loan term in years:");
			var loanTermInYears = in.nextInt();

			System.out.println("");
			var plan = loanCalculator.calculatePaymentPlan(LoanType.HOUSING, loanAmount, loanTermInYears);
			System.out.println("Your payment plan is:");
			System.out.println(FlipTableConverters.fromIterable(plan, MonthlyPayment.class));

			System.out.println("");
			System.out.println("Type Y to repeat:");
			in.nextLine(); // ignore last enter from nexInt read
			var response = in.nextLine();
			if (!"Y".equalsIgnoreCase(response)) {
				return;
			}
		}
	}

}
