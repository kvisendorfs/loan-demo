package lv.kvisendorfs.loandemo;

import static java.math.BigDecimal.ONE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedInterestLoanCalculator {

	private final static MathContext USED_MATH_CONTEXT = MathContext.DECIMAL128;
	private final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
	private final static int MONTHS_IN_YEAR = 12;

	private final InterestRateService interestRateService;

	public List<MonthlyPayment> calculatePaymentPlan(LoanType loanType, BigDecimal loanAmount, int loanTermInYears) {
		var annualInterestRate = interestRateService.getInterestRateForLoanType(loanType);
		var monthlyInterestRate = getMonthlyInterestRate(annualInterestRate);
		var loanTermInMonths = loanTermInYears * MONTHS_IN_YEAR;
		var baseMonthlyPayment = getBaseMonthlyPayment(loanAmount, monthlyInterestRate, loanTermInMonths);

		BigDecimal remainingBalance = loanAmount;
		var paymentPlan = new ArrayList<MonthlyPayment>(loanTermInMonths);
		//TODO stream?
		for (int month = 1; month <= loanTermInMonths; month++) {
			//FIXME fix corner case with last payment
			var monthlyInterest = scaleToMoney(remainingBalance.multiply(monthlyInterestRate));
			var basePayment = scaleToMoney(baseMonthlyPayment.subtract(monthlyInterest));
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

	private BigDecimal getMonthlyInterestRate(BigDecimal annualInterestRate) {
		return annualInterestRate.divide(BigDecimal.valueOf(MONTHS_IN_YEAR), USED_MATH_CONTEXT);
	}

	private BigDecimal getBaseMonthlyPayment(BigDecimal loanAmount, BigDecimal monthlyInterestRate, int loanTermInMonths) {
		return loanAmount.multiply(monthlyInterestRate)
				.divide(ONE.subtract(ONE.add(monthlyInterestRate).pow(-loanTermInMonths, USED_MATH_CONTEXT)), ROUNDING_MODE);
	}

	private BigDecimal scaleToMoney(BigDecimal value) {
		return value.setScale(2, ROUNDING_MODE);
	}

}
