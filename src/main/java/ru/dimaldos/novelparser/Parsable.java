package ru.dimaldos.novelparser;

import org.openqa.selenium.WebDriver;

import java.util.List;

public interface Parsable {

    List<String> parse(WebDriver webDriver);

}
