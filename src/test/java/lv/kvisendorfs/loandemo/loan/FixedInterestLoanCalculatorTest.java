package lv.kvisendorfs.loandemo.loan;

import static lv.kvisendorfs.loandemo.loan.LoanType.HOUSING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lv.kvisendorfs.loandemo.rate.InterestRateService;

@ExtendWith(MockitoExtension.class)
public class FixedInterestLoanCalculatorTest {

	@Mock
	private InterestRateService interestRateService;
	private Validator validator;
	private FixedInterestLoanCalculator calculator;

	private ObjectMapper mapper;

	@BeforeEach
	public void setUp() {
		lenient().when(interestRateService.getInterestRateForLoanType(eq(HOUSING))).thenReturn(new BigDecimal("0.035"));
		validator = new Validator();
		calculator = new FixedInterestLoanCalculator(interestRateService, validator);
		mapper = new ObjectMapper();
	}

	@Test
	public void testTwelvePaymentsWithLastPaymentOverStandard() {
		var result = calculator.calculatePaymentPlan(HOUSING, BigDecimal.valueOf(1200L), 1);
		var expected = loadFromResources("classpath:oneYear1200.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@Test
	public void testTwelvePaymentsWithLastPaymentTwoCentsUnder() {
		var result = calculator.calculatePaymentPlan(HOUSING, BigDecimal.valueOf(2000L), 1);
		var expected = loadFromResources("classpath:oneYearTwoCentsUnder.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@Test
	public void testLoanAmountTooLarge() {
		assertThatThrownBy(() -> calculator.calculatePaymentPlan(HOUSING, BigDecimal.valueOf(Long.MAX_VALUE), 1))
				.isInstanceOf(LoanArgumentException.class)
				.hasMessage("Invalid loan amount");
	}

	@Test
	public void testLoanTermTooLarge() {
		assertThatThrownBy(() -> calculator.calculatePaymentPlan(HOUSING, new BigDecimal("100"), Integer.MAX_VALUE))
				.isInstanceOf(LoanArgumentException.class)
				.hasMessage("Invalid loan term");
	}

	@Test
	public void testMaxValues() {
		var result = calculator.calculatePaymentPlan(HOUSING, new BigDecimal("9999999999"), 50);
		assertThat(result.get(599).getRemaining()).isEqualByComparingTo("0");
	}

	@Test
	public void testSmallAmount() {
		var result = calculator.calculatePaymentPlan(HOUSING, new BigDecimal("0.10"), 1);
		var expected = loadFromResources("classpath:smallAmount.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@Test
	public void testMinValues() {
		var result = calculator.calculatePaymentPlan(HOUSING, new BigDecimal("0.01"), 1);
		var expected = loadFromResources("classpath:min.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@Test
	public void testNonMoneyValues() {
		assertThatThrownBy(() -> calculator.calculatePaymentPlan(HOUSING, new BigDecimal("0.01356"), 1))
				.isInstanceOf(LoanArgumentException.class)
				.hasMessage("Invalid loan amount");
	}

	@Test
	public void testNullValues() {
		assertThatThrownBy(() -> calculator.calculatePaymentPlan(HOUSING, null, 0))
				.isInstanceOf(LoanArgumentException.class)
				.hasMessage("Invalid loan amount");

	}

	@SneakyThrows
	private List<MonthlyPayment> loadFromResources(String path) {
		return mapper.readValue(ResourceUtils.getFile(path),
				new TypeReference<List<MonthlyPayment>>() {
				});
	}
}
