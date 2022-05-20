package br.com.unb.cic.scrapy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
//	#find year of first commit by repository - https://api.github.com/repos/kde/snapcraft-kde-applications/commits?until=2017-01-01T00:00:00Z&per_page=1
	
	public static final int DEFAULT_BUFFER_SIZE = 8192;

	@SuppressWarnings("deprecation")
	public static ArrayList<Project> scrapper(ArrayList<String> projects, String organization)
			throws InterruptedException {

		ArrayList<Project> dataset = new ArrayList<Project>();

		for (String project : projects) {
			try {
				System.setProperty("webdriver.chrome.driver", IOEnum.PATH_CHROME_DRIVER.getProperty());
				ChromeOptions opt = new ChromeOptions();
				opt.addArguments("headless");
				WebDriver driver = new ChromeDriver(opt);
				driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
				driver.get(project);
				System.out.println(project);
				int count = 0;
				do {
					System.out.println("wait a seconds...");
					Thread.sleep(6000);
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

					String maxKey = Collections
							.max(languages.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();

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

					ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
					LocalDate pushed = zonedDateTime.toLocalDate();

					Integer commits = Integer.parseInt(cmts.replaceAll(",", ""));

					String apiUrl = "https://api.github.com/repos/" + organization.toLowerCase() + "/" + projectName;
					try {
						URL url = new URL(apiUrl);
						HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
						httpConn.setRequestMethod("GET");
						httpConn.setRequestProperty("Accept", "application/vnd.github.v3+json");
						byte[] message = (IOEnum.USER_TOKEN.getProperty()).getBytes("UTF-8");
						String basicAuth = Base64.getEncoder().encodeToString(message);
						httpConn.setRequestProperty("Authorization", "Basic " + basicAuth);
						try (InputStream is = httpConn.getInputStream();
								Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

							Gson gson = new Gson();
							GitRepository gr = gson.fromJson(reader, GitRepository.class);

							List<Committer> commitDates = new ArrayList<>();
							Integer yearNumber = 1995;
							do {
								try {
									String findTheYearFromOldestCommit = "https://api.github.com/repos/"
											+ organization.toLowerCase() + "/" + projectName + "/commits?until="
											+ yearNumber + "-01-01T00:00:00Z&per_page=1";
									URL url2 = new URL(findTheYearFromOldestCommit);
									HttpURLConnection httpConn2 = (HttpURLConnection) url2.openConnection();
									httpConn2.setRequestMethod("GET");
									httpConn2.setRequestProperty("Accept", "application/vnd.github.v3+json");
									byte[] message2 = (IOEnum.USER_TOKEN.getProperty()).getBytes("UTF-8");
									String basicAuth2 = Base64.getEncoder().encodeToString(message2);
									httpConn2.setRequestProperty("Authorization", "Basic " + basicAuth2);

									try (InputStream is2 = httpConn2.getInputStream()) {
										String reader2 = removeFirstandLast(convertInputStreamToString(is2));
										Gson gson2 = new Gson();
										GitCommit gc = gson2.fromJson(reader2, GitCommit.class);
										if (gc != null) {
											commitDates.add(gc.getCommit().getCommitter());
											yearNumber++;
										} else {
											yearNumber++;
										}
									} catch (Exception e) {
										yearNumber++;
										e.printStackTrace(System.out);
									}

								} catch (Exception e) {
									yearNumber++;
									e.printStackTrace(System.out);
								}
							} while (yearNumber == 2022);

							LocalDate created = null;

							if (!commitDates.isEmpty()) {
								List<LocalDate> cmtd = commitDates.stream().map(t -> convertToLocalDate(t.getDate()))
										.collect(Collectors.toList());
								created = cmtd.stream().min(LocalDate::compareTo).get();
							} else {
								zonedDateTime = ZonedDateTime.parse(gr.getCreated_at());
								created = zonedDateTime.toLocalDate();
							}

							Project pj = new Project(organization.toLowerCase(), projectName, gitUrl, stars, commits,
									contribs, gr.getFork(), created, pushed, languages.get(maxKey), maxKey);
							dataset.add(pj);
						} catch (Exception e) {
							e.printStackTrace(System.out);
						}

					} catch (Exception e) {
						e.printStackTrace(System.out);
					}

				}
				driver.quit();
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
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
			return Double.parseDouble(string);
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

	public static class GitCommit {
		private String node_id;
		private Commit commit;

		public String getNode_id() {
			return node_id;
		}

		public Commit getCommit() {
			return commit;
		}
	}
	
	public static class Commit{
		private Committer committer;
		
		public Committer getCommitter() {
			return committer;
		}
	}

	public static class Committer {
		private String name;
		private String date;

		public String getName() {
			return name;
		}
		
		public String getDate() {
			return date;
		}
	}

	public static LocalDate convertToLocalDate(String date) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(date);
		return zonedDateTime.toLocalDate();
	}

	private static String convertInputStreamToString(InputStream is) throws IOException {

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int length;
		while ((length = is.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}

		return result.toString(StandardCharsets.UTF_8);
	}

	public static String removeFirstandLast(String str) {
		StringBuilder sb = new StringBuilder(str);
		sb.deleteCharAt(str.length() - 1);
		sb.deleteCharAt(0);
		return sb.toString();
	}

}
