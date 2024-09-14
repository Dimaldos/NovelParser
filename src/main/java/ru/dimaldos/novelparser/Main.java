package ru.dimaldos.novelparser;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(500, 800));
        driver.get("https://www.sousetsuka.com/p/blog-page.html");
        List<String> links = driver.findElement(By.id("post-body-915275761407871263"))
                .findElements(By.tagName("a")).stream()
                .filter(element -> !element.getText().startsWith("Dream") && !element.getText().startsWith("http"))
                .map(e -> e.getAttribute("href"))
                .toList();
        driver.quit();
        System.out.println(links.size());
        List<List<ChapterRequest>> jobs = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            jobs.add(new ArrayList<>());
        }
        for (int i = 0; i < links.size(); i++) {
            jobs.get(i / 100).add(new ChapterRequest(links.get(i), i + 1));
        }
        for (List<ChapterRequest> job : jobs) {
            new Thread(new Worker(
                    d -> Arrays.stream(d.findElement(By.className("post-body"))
                                    .getText().split("\n\n"))
                            .toList()
                            .stream()
                            .filter(p -> !p.startsWith("Synopsis:") &&
                                    !p.startsWith("Chapter") &&
                                    !p.startsWith("Previous Chapter") &&
                                    !p.startsWith("Next Chapter"))
                            .map(String::trim)
                            .toList(),
                    job)).start();
        }
    }
}