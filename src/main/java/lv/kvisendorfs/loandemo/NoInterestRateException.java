package lv.kvisendorfs.loandemo;

public class NoInterestRateException extends RuntimeException {

	public NoInterestRateException(){
		super("Missing interest rate");
	}

}
