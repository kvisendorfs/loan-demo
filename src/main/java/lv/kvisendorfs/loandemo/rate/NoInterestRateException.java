package lv.kvisendorfs.loandemo.rate;

public class NoInterestRateException extends RuntimeException {

	public NoInterestRateException(){
		super("Missing interest rate");
	}

}
