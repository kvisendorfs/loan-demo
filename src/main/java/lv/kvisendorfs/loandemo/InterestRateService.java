package lv.kvisendorfs.loandemo;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class InterestRateService {

	//TODO tests
	public BigDecimal getInterestRateForLoanType(LoanType loanType) {
		BigDecimal interestRate;
		switch (loanType) {
		case HOUSING:
			interestRate = new BigDecimal("0.035");
			break;
		default:
			throw new NoInterestRateException();
		}
		return interestRate;
	}
}
