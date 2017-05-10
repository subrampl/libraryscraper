package com.subbu.selenium;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class ScraperTest {

	@Test
	public void testGoogle() throws Exception {
		HtmlUnitDriver  driver = new HtmlUnitDriver();
		driver.get("http://sccl.evanced.info/signup/List?ag=all&private=0&lib=3");
		
		CSVTable csvTable = new CSVTable("test.csv", Arrays.asList("eventId","title","date","AgeGroups","EventTypes","descriptionContainer"));
		
		/*URL url = new URL("http://www.example.com");
		StringWebResponse response = new StringWebResponse("<html><head><title>Test</title></head><body></body></html>", url);
		WebClient client = new WebClient();
		HtmlPage page = HTMLParser.parseHtml(response, client.getCurrentWindow());
		System.out.println(page.getTitleText());
		
		if(true)
			return;*/
		
		List<String> eventIds = new ArrayList<>(); 
		driver.findElements(By.className("eventTicketInfo")).forEach((e) -> eventIds.add(e.getAttribute("data-event-id")));
		
		for (String eventId : eventIds) {
			
			//System.out.println(eventId);
			driver.get("http://sccl.evanced.info/signup/EventDetails?EventId=" + eventId );
			
			if(getInfo(driver.findElements(By.className("detailInfoItem")), "AgeGroups").equals("SchoolAge")){
				CSVTableRow csvTableRow = new CSVTableRow(Arrays.asList("eventId","title","date","AgeGroups","EventTypes","description"));
				csvTableRow.setCell("eventId", eventId);
				csvTableRow.setCell("title", driver.findElement(By.className("eventTicketInfo")).getAttribute("data-event-title"));
				csvTableRow.setCell("date", driver.findElement(By.className("eventTicketInfo")).getAttribute("data-mail-modal-date"));
				csvTableRow.setCell("AgeGroups", getInfo(driver.findElements(By.className("detailInfoItem")), "AgeGroups"));
				csvTableRow.setCell("EventTypes", getInfo(driver.findElements(By.className("detailInfoItem")), "EventTypes"));
				csvTableRow.setCell("description", driver.findElement(By.className("descriptionContainer")).getText());
				
				System.out.println("************************************************************");
				System.out.println(driver.findElement(By.className("eventTicketInfo")).getAttribute("data-event-title"));
				System.out.println(driver.findElement(By.className("eventTicketInfo")).getAttribute("data-mail-modal-date"));
				System.out.println(getInfo(driver.findElements(By.className("detailInfoItem")), "AgeGroups"));
				System.out.println(getInfo(driver.findElements(By.className("detailInfoItem")), "EventTypes"));
				System.out.println(driver.findElement(By.className("descriptionContainer")).getText());
				csvTable.addRow(csvTableRow);
			}
		}
		
		csvTable.save();
		driver.quit();

	}
	

	private String getInfo(List<WebElement> elements, String elementName){
		String ret = "";
		
		for (WebElement webElement : elements) {
			String value = webElement.getText().replaceAll("[^a-zA-Z0-9]", "");
			if(value.startsWith(elementName)){
				ret = value.replace(elementName, "");
			}
		}
		return ret;
	}
}
