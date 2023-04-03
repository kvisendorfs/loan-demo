package lv.kvisendorfs.loandemo.rate;

import static lv.kvisendorfs.loandemo.loan.LoanType.HOUSING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InterestRateServiceTest {

	private static final String DEFAULT_RATE = "0.035";

	@Autowired
	private InterestRateService rateService;

	@BeforeEach
	public void setUp() {
		rateService = new InterestRateService();
		rateService.housingLoanInterestRate = new BigDecimal(DEFAULT_RATE);
	}

	@Test
	public void testDefaultConfiguration() {
		var rate = rateService.getInterestRateForLoanType(HOUSING);
		assertThat(rate).isEqualByComparingTo(DEFAULT_RATE);
	}

	@Test
	public void testNull() {
		assertThatThrownBy(() -> rateService.getInterestRateForLoanType(null))
				.isInstanceOf(NoInterestRateException.class);
	}

}
