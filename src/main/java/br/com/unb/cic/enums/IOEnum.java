package br.com.unb.cic.enums;

public enum IOEnum {
	
	PATH_CHROME_DRIVER("D:\\chromedriver.exe"),
//	PATH_CHROME_DRIVER("/usr/local/bin/chromedriver"),
	PATH_REPOSITORIES("D:\\projects.csv"),
	USER_TOKEN(System.getenv("GITOKEN"));
	
	
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
