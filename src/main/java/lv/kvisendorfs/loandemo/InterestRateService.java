package lv.kvisendorfs.loandemo;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InterestRateService {

	@Value("${rates.housing}")
	protected BigDecimal housingLoanInterestRate;

	public BigDecimal getInterestRateForLoanType(LoanType loanType) {
		if (loanType == null) {
			throw new NoInterestRateException();
		}
		BigDecimal interestRate;
		switch (loanType) {
		case HOUSING:
			interestRate = housingLoanInterestRate;
			break;
		default:
			throw new NoInterestRateException();
		}
		return interestRate;
	}
}
