package com.usesoft.poker.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
		Document doc = Jsoup.connect("https://www.winamax.fr/les-challenges-winamax_cash-game_classement-micro-limites").get();
		Elements newsHeadlines = doc.select(".table_container");
		
		resp.getWriter().println(newsHeadlines.toString());
	}
}
