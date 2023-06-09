package lv.kvisendorfs.loandemo.loan;

import static java.math.BigDecimal.ONE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.kvisendorfs.loandemo.rate.InterestRateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedInterestLoanCalculator {

	private final static MathContext USED_MATH_CONTEXT = MathContext.DECIMAL128;
	private final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
	private final static int MONTHS_IN_YEAR = 12;

	private final InterestRateService interestRateService;
	private final Validator validator;

	public List<MonthlyPayment> calculatePaymentPlan(LoanType loanType, BigDecimal loanAmount, int loanTermInYears) {
		validateLoanAmount(loanAmount);
		validateLoanTerm(loanTermInYears);

		var annualInterestRate = interestRateService.getInterestRateForLoanType(loanType);
		var monthlyInterestRate = getMonthlyInterestRate(annualInterestRate);
		var loanTermInMonths = loanTermInYears * MONTHS_IN_YEAR;
		var monthlyPayment = getBaseMonthlyPayment(loanAmount, monthlyInterestRate, loanTermInMonths);
		var remainingBalance = loanAmount;

		var paymentPlan = new ArrayList<MonthlyPayment>(loanTermInMonths);
		for (int month = 1; month <= loanTermInMonths; month++) {
			var monthlyInterest = scaleToMoney(remainingBalance.multiply(monthlyInterestRate));
			var basePayment = calculateMonthlyBasePayment(loanTermInMonths, monthlyPayment, remainingBalance, month, monthlyInterest);

			remainingBalance = scaleToMoney(remainingBalance.subtract(basePayment));

			paymentPlan.add(
					MonthlyPayment.builder()
							.month(month)
							.interest(monthlyInterest)
							.base(basePayment)
							.total(basePayment.add(monthlyInterest))
							.remaining(remainingBalance)
							.build());
		}
		return paymentPlan;
	}

	private void validateLoanAmount(BigDecimal loanAmount) {
		if (!validator.isLoanAmountValid(loanAmount)) {
			log.error("Invalid loan amount : {}", loanAmount);
			throw new LoanArgumentException("Invalid loan amount");
		}
	}

	private void validateLoanTerm(int loanTermInYears) {
		if (!validator.isLoanTermValid(loanTermInYears)) {
			log.error("Invalid loan term : {}", loanTermInYears);
			throw new LoanArgumentException("Invalid loan term");
		}
	}

	private BigDecimal calculateMonthlyBasePayment(int loanTermInMonths, BigDecimal monthlyPayment, BigDecimal remainingBalance, int month,
			BigDecimal monthlyInterest) {
		var basePayment = scaleToMoney(monthlyPayment.subtract(monthlyInterest));
		if (isLastPaymentTunable(loanTermInMonths, remainingBalance, month, basePayment)
				|| isPaymentLargerThanRemainder(remainingBalance, basePayment)) {
			basePayment = remainingBalance;
		}
		return basePayment;
	}

	private boolean isLastPaymentTunable(int loanTermInMonths, BigDecimal remainingBalance, int month, BigDecimal basePayment) {
		return isLastPayment(loanTermInMonths, month) && basePayment.compareTo(remainingBalance) != 0;
	}

	private boolean isPaymentLargerThanRemainder(BigDecimal remainingBalance, BigDecimal basePayment) {
		return basePayment.compareTo(remainingBalance) > 0;
	}

	private boolean isLastPayment(int loanTermInMonths, int month) {
		return month == loanTermInMonths;
	}

	private BigDecimal getMonthlyInterestRate(BigDecimal annualInterestRate) {
		return annualInterestRate.divide(BigDecimal.valueOf(MONTHS_IN_YEAR), USED_MATH_CONTEXT);
	}

	private BigDecimal getBaseMonthlyPayment(BigDecimal loanAmount, BigDecimal monthlyInterestRate, int loanTermInMonths) {
		return loanAmount.multiply(monthlyInterestRate)
				.divide(ONE.subtract(ONE.add(monthlyInterestRate).pow(-loanTermInMonths, USED_MATH_CONTEXT)), 2, ROUNDING_MODE);
	}

	private BigDecimal scaleToMoney(BigDecimal value) {
		return value.setScale(2, ROUNDING_MODE);
	}

}
