package lv.kvisendorfs.loandemo;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
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
		when(interestRateService.getInterestRateForLoanType(eq(LoanType.HOUSING))).thenReturn(new BigDecimal("0.035"));
	}

	@SneakyThrows
	@Test
	public void testSimpleCase() {
		var result = calculator.calculatePaymentPlan(LoanType.HOUSING, BigDecimal.valueOf(1200L), 1);
		var expected = mapper.readValue(ResourceUtils.getFile("classpath:lv/kvisendorfs/loandemo/oneYear1200.json"),
				new TypeReference<List<MonthlyPayment>>() {
				});
		Assertions.assertThat(result).containsExactlyElementsOf(expected);
	}
}
