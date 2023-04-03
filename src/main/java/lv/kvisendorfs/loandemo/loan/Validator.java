package lv.kvisendorfs.loandemo.loan;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class Validator {

	public boolean isLoanAmountValid(BigDecimal loanAmount) {
		return loanAmount != null
				&& loanAmount.compareTo(BigDecimal.ZERO) > 0
				&& loanAmount.compareTo(new BigDecimal("9999999999")) <= 0
				&& getNumberOfDecimalPlaces(loanAmount) <= 2;
	}

	private int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
		return Math.max(0, bigDecimal.stripTrailingZeros().scale());
	}

	public String getLoanAmountValidationMessage() {
		return "Loan amount should be between 0 and 9999999999";
	}

	public boolean isLoanTermValid(Integer loanTerm) {
		return loanTerm != null && loanTerm > 0 && loanTerm <= 50;
	}

	public String getLoanTermValidationMessage() {
		return "Loan term should be between 1 and 50";
	}
}
