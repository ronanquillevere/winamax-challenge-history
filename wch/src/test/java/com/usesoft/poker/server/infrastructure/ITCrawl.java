package com.usesoft.poker.server.infrastructure;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.player.Player;

public class ITCrawl
{
    @Test
    public void testMicro() throws IOException, URISyntaxException, ParseException
    {
        Document document = parseDocument("Challenge Cash Game - Micro limites - Winamax Poker.html");
        check(document, Stake.Micro, 1383260400000l, 1383865199999l, 100, 100, getTestFile("microPerfs.json"));
    }

    @Test
    public void testSmall() throws IOException, URISyntaxException, ParseException
    {
        Document document = parseDocument("Challenge Cash Game - Basses limites - Winamax Poker.html");
        check(document, Stake.Small, 1383260400000l, 1383865199999l, 100, 100, getTestFile("smallPerfs.json"));
    }

    @Test
    public void testMedium() throws IOException, URISyntaxException, ParseException
    {
        Document document = parseDocument("Challenge Cash Game - Moyennes limites - Winamax Poker.html");
        check(document, Stake.Medium, 1383260400000l, 1383865199999l, 100, 100, getTestFile("mediumPerfs.json"));
    }

    @Test
    public void testHigh() throws IOException, URISyntaxException, ParseException
    {
        Document document = parseDocument("Challenge Cash Game - Hautes limites - Winamax Poker.html");
        check(document, Stake.High, 1383260400000l, 1383865199999l, 29, 29, getTestFile("highPerfs.json"));
    }

    private void check(Document document, Stake stake, long start, long end, int nbPlayers, int nbPerfs, File expectedJSonOutputPerfs) throws ParseException,
    JsonGenerationException,
    JsonMappingException, IOException
    {
        Date timestamp = CrawlerUtil.getParisTime();
        // Extraction phase
        List<CashGamePerformance> perfs = new ArrayList<CashGamePerformance>();
        List<Player> players = new ArrayList<Player>();
        Period period = CrawlerUtil.extractPeriod(document);
        CrawlerUtil.fillPlayersAndPerfs(document, stake, period, timestamp, perfs, players);

        assertThat(perfs.size()).isEqualTo(nbPerfs);
        assertThat(players.size()).isEqualTo(nbPlayers);
        assertThat(period.getStart()).isEqualTo(new Date(start));
        assertThat(period.getEnd()).isEqualTo(new Date(end));

        ObjectMapper mapper = new ObjectMapper();
        File file = File.createTempFile("testWCH", ".json");
        mapper.writeValue(file, perfs);

        assertThat(file.length()).isEqualTo(expectedJSonOutputPerfs.length());

    }

    private Document parseDocument(String fileName) throws IOException, URISyntaxException
    {
        File f = getTestFile(fileName);
        return Jsoup.parse(f, null);
    }

    private File getTestFile(String fileName) throws URISyntaxException
    {
        URL resource = getClass().getResource(fileName);
        File f = new File(resource.toURI().getPath());
        return f;
    }
}
