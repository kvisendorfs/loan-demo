package lv.kvisendorfs.loandemo.ui;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.jakewharton.fliptables.FlipTableConverters;

import lombok.RequiredArgsConstructor;
import lv.kvisendorfs.loandemo.loan.FixedInterestLoanCalculator;
import lv.kvisendorfs.loandemo.loan.LoanType;
import lv.kvisendorfs.loandemo.loan.MonthlyPayment;
import lv.kvisendorfs.loandemo.loan.Validator;

@Service
@RequiredArgsConstructor
public class UserInputService {

	private final FixedInterestLoanCalculator loanCalculator;
	private final Validator validator;

	public void communicateWithUser() {
		printTitle();
		while (true) {
			var loanAmount = getLoanAmount();
			int loanTermInYears = getLoanTermInYears();
			printPaymentPlan(loanAmount, loanTermInYears);
			if (doesUserWantToExit()) {
				return;
			}
		}
	}

	private void printTitle() {
		System.out.println("");
		System.out.println("Loan Demo Application");
		System.out.println("");
	}

	private BigDecimal getLoanAmount() {
		System.out.println("Input loan amount:");
		return getUserInput(
				(in -> in.hasNextBigDecimal())
				, (in -> in.nextBigDecimal())
				, validator::isLoanAmountValid,
				validator.getLoanAmountValidationMessage());
	}

	private int getLoanTermInYears() {
		System.out.println("Input loan term in years:");
		return getUserInput(
				(in -> in.hasNextInt())
				, (in -> in.nextInt())
				, validator::isLoanTermValid,
				validator.getLoanTermValidationMessage());
	}

	private <T extends Number> T getUserInput(Predicate<Scanner> hasValidDataType, Function<Scanner, T> inputGetter,
			Predicate<T> validator, String validationErrorMessage) {
		Scanner in = clearBuffer();
		T value = null;
		while (value == null) {
			if (hasValidDataType.test(in)) {
				value = inputGetter.apply(in);
				if (!validator.test(value)) {
					value = null;
					System.out.println(validationErrorMessage);
				}
			} else {
				printInvalidInput();
				in = clearBuffer();
			}
		}
		return value;
	}

	private void printInvalidInput() {
		System.out.println("Invalid input");
	}

	private void printPaymentPlan(BigDecimal loanAmount, int loanTermInYears) {
		System.out.println("");
		var plan = loanCalculator.calculatePaymentPlan(LoanType.HOUSING, loanAmount, loanTermInYears);
		System.out.println("Your payment plan is:");
		System.out.println(FlipTableConverters.fromIterable(plan, MonthlyPayment.class));
	}

	private boolean doesUserWantToExit() {
		Scanner in = clearBuffer();
		System.out.println("");
		System.out.println("Type Y to repeat:");
		return !"Y".equalsIgnoreCase(in.nextLine());
	}

	private Scanner clearBuffer() {
		return new Scanner(System.in);
	}
}
