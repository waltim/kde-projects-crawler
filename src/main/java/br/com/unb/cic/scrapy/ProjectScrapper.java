package br.com.unb.cic.scrapy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.gson.Gson;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.enums.IOEnum;

public class ProjectScrapper {

//	#find by organization - https://api.github.com/orgs/kde

//	#find repos by organization - https://api.github.com/repos/kde/kscreenlocker

	@SuppressWarnings("deprecation")
	public static ArrayList<Project> scrapper(ArrayList<String> projects, String organization)
			throws InterruptedException {

		ArrayList<Project> dataset = new ArrayList<Project>();

		for (String project : projects) {
			System.setProperty("webdriver.chrome.driver", IOEnum.PATH_CHROME_DRIVER.getProperty());
			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("headless");
			WebDriver driver = new ChromeDriver(opt);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.get(project);
			System.out.println(project);
			int count = 0;
			do {
				System.out.println("wait a seconds...");
				Thread.sleep(5000);
				if (!driver.findElements(By.xpath("//*[contains(text(),'This repository is empty.')]")).isEmpty()) {
					driver.quit();
					count = 100;
					break;
				}
				count++;
				if (count > 2) {
					driver.quit();
					count = 100;
					break;
				}
			} while (driver.findElements(By.className("Box-row--focus-gray")).isEmpty());
			if (count == 100) {
				continue;
			}
			Document page = Jsoup.parse(driver.getPageSource());
			Elements body = page.select("div.BorderGrid-cell");
			Element ulist = body.select("ul.list-style-none").last();
			Integer contribs = 0;

			if (body != null) {

				contribs = Integer.parseInt(body.select("span.Counter").last().attr("title"));

				Map<String, Double> languages = new HashMap<String, Double>();

				if (ulist != null) {
					for (Element element : ulist.select("li.d-inline")) {
						String language = element.select("span.color-fg-default").text();
						String width = element.select("span").last().text();
						Double percent = Double.parseDouble(width.replace("%", ""));
						languages.put(language, percent);
					}
				} else {
					System.out.println("languages error in repository: " + project);
				}

				if (languages.isEmpty()) {
					languages.put("N/A", 0.0);
				}

				String maxKey = Collections.max(languages.entrySet(), Comparator.comparingDouble(Map.Entry::getValue))
						.getKey();

				String projectName = page.select("strong.flex-self-stretch").first().text();

				Element gitLink = page.select("div.input-group").first();
				String gitUrl = gitLink.select("input.input-monospace").attr("value");

				Element borderSpacious = page.select("div.BorderGrid--spacious").first();
				Element borderRow = borderSpacious.select("div.BorderGrid-row").first();
				Elements borders = borderRow.select("div.mt-2");

				Double stars = 0.0;
				if (!borders.isEmpty()) {
					for (Element div : borders) {
						if (div.select("a.Link--muted").text().contains("star")) {
							String star = div.select("a.Link--muted").text().replaceAll(" stars", "")
									.replaceAll(" star", "").trim();
							stars = convertToLargerNumber(star);
						}
					}
				} else {
					System.out.println("starts error in repository: " + project);
				}

				String dateTime = null;
				String cmts = null;

				Element divJs = page.select("div.js-details-container").first();
				dateTime = divJs.selectFirst("div.flex-items-baseline").selectFirst("relative-time.no-wrap")
						.attr("datetime");
				cmts = divJs.selectFirst("div.flex-shrink-0").lastElementSibling().selectFirst("strong").text();

				String[] separatedDate = dateTime.split("T");
				String[] dateSplit = separatedDate[0].split("-");
				LocalDate pushed = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]),
						Integer.parseInt(dateSplit[2]));
				Integer commits = Integer.parseInt(cmts.replaceAll(",", ""));

				String apiUrl = "https://api.github.com/repos/" + organization.toLowerCase() + "/" + projectName;
				try {
					URL url = new URL(apiUrl);
					HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setRequestMethod("GET");
					httpConn.setRequestProperty("Accept", "application/json");
					byte[] message = (IOEnum.USER_TOKEN.getProperty()).getBytes("UTF-8");
					String basicAuth = Base64.getEncoder().encodeToString(message);
					httpConn.setRequestProperty("Authorization", "Basic " + basicAuth);
					try (InputStream is = httpConn.getInputStream();
							Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

						Gson gson = new Gson();
						GitRepository gr = gson.fromJson(reader, GitRepository.class);

						separatedDate = gr.getCreated_at().split("T");
						dateSplit = separatedDate[0].split("-");
						LocalDate created = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]),
								Integer.parseInt(dateSplit[2]));

						Project pj = new Project(organization.toLowerCase(), projectName, gitUrl, stars, commits,
								contribs, gr.getFork(), created, pushed, languages.get(maxKey), maxKey);
						dataset.add(pj);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
			driver.quit();

		}
		return dataset;
	}

	public static Double convertToLargerNumber(String string) {
		String multiplier = string.substring(string.length() - 1).toLowerCase();
		if (multiplier.equals("k"))
			return Double.parseDouble(string.substring(0, string.length() - 1)) * 1000;
		else if (multiplier.equals("m"))
			return Double.parseDouble(string.substring(0, string.length() - 1)) * 1000000;
		else
			return 0.0;
	}

	public static class GitRepository {
		private Boolean fork;
		private String created_at;
		private Integer forks_count;

		public Boolean getFork() {
			return fork;
		}

		public String getCreated_at() {
			return created_at;
		}

		public Integer getForks_count() {
			return forks_count;
		}
	}

}
