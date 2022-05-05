package br.com.unb.cic.enums;

public enum IOEnum {
	
	PATH_CHROME_DRIVER("D:\\chromedriver.exe");
	
	private IOEnum(String property) {
		this.property = property;
	}
	
	private String property;

	public String getProperty() {
		return property;
	}
	
	public void setProperty(String property) {
		this.property = property;
	}
}
