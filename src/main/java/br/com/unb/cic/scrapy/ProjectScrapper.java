package br.com.unb.cic.scrapy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.enums.IOEnum;

public class ProjectScrapper {

//	#find by organization - https://api.github.com/orgs/kde

//	#find repos by organization - https://api.github.com/repos/kde/kscreenlocker

	public static ArrayList<Project> scrapper(ArrayList<String> projects, String organization) {

		ArrayList<Project> dataset = new ArrayList<Project>();

		for (String project : projects) {
			System.setProperty("webdriver.chrome.driver", IOEnum.PATH_CHROME_DRIVER.getProperty());
			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("headless");
			WebDriver driver = new ChromeDriver(opt);
			driver.get(project);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Document page = Jsoup.parse(driver.getPageSource());
			Elements body = page.select("div.BorderGrid-cell");
			Element ulist = body.select("ul.list-style-none").last();

			if (body != null) {

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

				Integer stars = 0;
				if (!borders.isEmpty()) {
					for (Element div : borders) {
						if (div.select("a.Link--muted").text().contains("star")) {
							String star = div.select("a.Link--muted").text();
							stars = Integer.parseInt(stripNonDigits(star));
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

				Project pj = new Project(projectName, gitUrl, stars, commits, languages.get(maxKey), maxKey, pushed);
				dataset.add(pj);

//				String apiUrl = "https://api.github.com/repos/" + organization.toLowerCase() + "/" + projectName;
//
//				try (InputStream is = new URL(apiUrl).openStream();
//						Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
//
//					Gson gson = new Gson();
//					GitRepository gr = gson.fromJson(reader, GitRepository.class);
//
//					separatedDate = gr.getCreated_at().split("T");
//					dateSplit = separatedDate[0].split("-");
//					LocalDate created = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]),
//							Integer.parseInt(dateSplit[2]));
//
//					Project pj = new Project(projectName, gitUrl, stars, commits, gr.getFork(), created, pushed,
//							languages.get(maxKey), maxKey);
//					dataset.add(pj);
//				} catch (Exception e) {
//					System.out.println(e.getMessage());
//				}

			}
			driver.close();

		}
		return dataset;
	}

	private static String stripNonDigits(final CharSequence input) {
		final StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (c > 47 && c < 58) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

//	public static class GitRepository {
//		private Boolean fork;
//		private String created_at;
//		private Integer size;
//		private Integer forks_count;
//
//		public Boolean getFork() {
//			return fork;
//		}
//
//		public String getCreated_at() {
//			return created_at;
//		}
//
//		public Integer getSize() {
//			return size;
//		}
//
//		public Integer getForks_count() {
//			return forks_count;
//		}
//	}

}
