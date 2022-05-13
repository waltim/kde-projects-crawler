package br.com.unb.cic.scrapy;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import br.com.unb.cic.enums.IOEnum;

public class ProjectCrawler {

	public static ArrayList<String> crawler(String url, String organization) throws InterruptedException {

		ArrayList<String> projects = new ArrayList<String>();
		Integer pages = 1;
		Boolean newPage = false;

		do {
			newPage = false;
			System.setProperty("webdriver.chrome.driver", IOEnum.PATH_CHROME_DRIVER.getProperty());
			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("headless");
			WebDriver driver = new ChromeDriver(opt);

			if (pages > 1) {
				url = "https://github.com/orgs/" + organization.toUpperCase() + "/repositories?page=" + pages;
			}
			driver.get(url);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Document doc = Jsoup.parse(driver.getPageSource());
			Element div = doc.selectFirst("div.repo-list");
			Element body = div.selectFirst("ul");

			for (Element e : body.select("li")) {

				Element link = e.selectFirst("a.d-inline-block");
				String href = link.attr("href");
				String project = "https://github.com" + href;
				projects.add(project);
			}

			Elements pagination = doc.select("div.pagination");

			for (Element e : pagination.select("a")) {
				String rel = e.attr("rel");
				if (rel.equals("next")) {
					newPage = true;
				}
			}

			if (newPage) {
				pages++;
			}

			driver.close();
		} while (newPage);
		System.out.println("Number of pages crawled: " + pages);
		return projects;
	}

}
