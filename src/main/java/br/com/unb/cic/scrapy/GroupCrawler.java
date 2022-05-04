package br.com.unb.cic.scrapy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class GroupCrawler {

	public static ArrayList<String> crawler(String url) throws InterruptedException {

		ArrayList<String> projects = new ArrayList<String>();
		Integer pages = urlNumberPages(url);
//		System.out.println(pages);
		if (pages != null) {
			String paginator = "?&page=";
			for (int i = 1; i < pages + 1; i++) {
				System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
				WebDriver driver = new ChromeDriver();
				String urlPage = url + paginator + i;
				System.out.println(urlPage);
				driver.get(urlPage);
				Thread.sleep(3000);
				Document doc = Jsoup.parse(driver.getPageSource());
				Elements body = doc.select("ul.groups-list");

				for (Element e : body.select("li")) {

					Element link = e.select("a.gl-display-none").first();
					String href = link.attr("href");
					String project = "https://invent.kde.org" + href;
					projects.add(project);
				}
				driver.close();
			}
		} else {
			System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
			WebDriver driver = new ChromeDriver();
			driver.get(url);
			Thread.sleep(3000);
			Document doc = Jsoup.parse(driver.getPageSource());
			Elements body = doc.select("ul.groups-list");

			for (Element e : body.select("li")) {

				Element link = e.select("a.gl-display-none").first();
				String href = link.attr("href");
				String project = "https://invent.kde.org" + href;
				projects.add(project);
			}
			driver.close();
		}

		return projects;
	}

	public static Map<String, String> crawler(ArrayList<String> groups) throws InterruptedException {

		Map<String, String> projects = new HashMap<String, String>();

		for (String url : groups) {
			Integer pages = urlNumberPages(url);
			if (pages != null) {
				String paginator = "?&page=";
				for (int i = 1; i < pages + 1; i++) {
					System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
					WebDriver driver = new ChromeDriver();
					String urlPage = url + paginator + i;
					driver.get(urlPage);
					Thread.sleep(3000);
					Document doc = Jsoup.parse(driver.getPageSource());
					Elements body = doc.select("ul.groups-list");

					for (Element e : body.select("li")) {

						Element link = e.select("a.gl-display-none").first();
						String href = link.attr("href");
						String name = link.attr("aria-label");
						String project = "https://invent.kde.org" + href;
						projects.put(name, project);
					}
					driver.close();
				}
			} else {
				System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
				WebDriver driver = new ChromeDriver();
				driver.get(url);
				Thread.sleep(3000);
				Document doc = Jsoup.parse(driver.getPageSource());
				Elements body = doc.select("ul.groups-list");

				for (Element e : body.select("li")) {

					Element link = e.select("a.gl-display-none").first();
					String href = link.attr("href");
					String name = link.attr("aria-label");
					String project = "https://invent.kde.org" + href;
					projects.put(name, project);
				}
				driver.close();
			}

		}

		return projects;
	}

	public static Integer urlNumberPages(String url) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.get(url);
		Thread.sleep(3000);
		Document doc = Jsoup.parse(driver.getPageSource());
		Elements body = doc.select("ul.pagination");
		if (body != null) {
			Integer count = 0;
			for (Element e : body.select("li")) {
				String data = e.select("a").attr("size");
				if (new String("md").equals(data)) {
					count++;
				}
			}
			driver.close();
			if (count > 0) {
				return count;
			} else {
				return null;
			}
		}
		driver.close();
		return null;
	}

}
