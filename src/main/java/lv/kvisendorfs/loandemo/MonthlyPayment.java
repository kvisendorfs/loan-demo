package lv.kvisendorfs.loandemo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonthlyPayment {

	private int month;
	private BigDecimal base;
	private BigDecimal interest;
	private BigDecimal total;
	private BigDecimal remaining;

}
