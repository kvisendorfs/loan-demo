package lv.kvisendorfs.loandemo;

import static lv.kvisendorfs.loandemo.LoanType.HOUSING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class FixedInterestLoanCalculatorTest {

	@Mock
	private InterestRateService interestRateService;
	@InjectMocks
	private FixedInterestLoanCalculator calculator;

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		when(interestRateService.getInterestRateForLoanType(eq(HOUSING))).thenReturn(new BigDecimal("0.035"));
	}

	@Test
	public void testTwelvePaymentsWithLastPaymentOverStandard() {
		var result = calculator.calculatePaymentPlan(HOUSING, BigDecimal.valueOf(1200L), 1);
		var expected = loadFromResources("classpath:lv/kvisendorfs/loandemo/oneYear1200.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@SneakyThrows
	@Test
	public void testTwelvePaymentsWithLastPaymentTwoCentsUnder() {
		var result = calculator.calculatePaymentPlan(HOUSING, BigDecimal.valueOf(2000L), 1);
		mapper.writer().writeValueAsString(result);
		var expected = loadFromResources("classpath:lv/kvisendorfs/loandemo/oneYearTwoCentsUnder.json");
		assertThat(result).containsExactlyElementsOf(expected);
	}

	@SneakyThrows
	private List<MonthlyPayment> loadFromResources(String path) {
		return mapper.readValue(ResourceUtils.getFile(path),
				new TypeReference<List<MonthlyPayment>>() {
				});
	}
}
