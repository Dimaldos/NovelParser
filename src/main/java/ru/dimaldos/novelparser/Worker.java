package ru.dimaldos.novelparser;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Worker implements Runnable {

    private final Parsable parser;
    private final List<ChapterRequest> requests;
    private final ChromeOptions options;
    private static String pageSample;

    public Worker(Parsable parser, List<ChapterRequest> requests) {
        this.parser = parser;
        this.options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--remote-allow-origins=*");
        this.requests = requests;

    }

    public static String getPageSample() throws IOException {
        if (pageSample == null) {
            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines(Path.of(AppProperties.getProperty("sample")))) {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            }
            pageSample = contentBuilder.toString();
        }
        return pageSample;
    }

    @Override
    public void run() {
        WebDriver driver = null;
        for (int i = 0; i < requests.size();) {
            try {
                ChapterRequest request = requests.get(i);
                driver = new ChromeDriver(options);
                driver.manage().window().setSize(new Dimension(500, 800));
                System.out.printf("Request: %d, %s%n", request.position(), request.link());
                driver.get(request.link());
                List<String> paragraphs = parser.parse(driver);
                driver.quit();
                String htmlText = "<div class=\"text\">"
                        + String.join("</div>\n<div class=\"text\">", paragraphs)
                        + "</div>";
                PrintWriter out = new PrintWriter(AppProperties.getProperty("output").formatted(request.position()),
                        StandardCharsets.UTF_8);
                out.println(getPageSample()
                        .replaceAll("&chapter&", "" + request.position())
                        .replaceAll("&content&", htmlText));
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        if (driver != null) {
            driver.quit();
        }
    }
}
