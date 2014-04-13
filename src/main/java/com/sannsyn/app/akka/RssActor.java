package com.sannsyn.app.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import com.sannsyn.app.Factory;
import com.sannsyn.app.models.RssEntry;

import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.ning.http.client.*;
import java.util.concurrent.Future;


import java.net.URL;
import java.io.InputStreamReader;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Created by petter on 12.04.14.
 */
public class RssActor extends UntypedActor {

    List<RssEntry> rssEntries = new ArrayList<RssEntry>();
    ActorRef httpActors;
    int numberOfActiveHttp = 0;
    int numberOfUrlsToDownload = 0;


    private void addAllRss() {
        rssEntries.clear();
        addOneRss("http://www.dagbladet.no/rss/nyheter/", "nyhter", "", "dagbladet");
    }

    /**
     *
     * @param url
     * @param category
     * @param subCategory
     * @param nickName
     *
     * Loads one rss-url, parses entries and adds to download
     *
     */
    private void addOneRss(String url, String category, String subCategory, String nickName) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(url)));
            for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
                RssEntry rssEntry = new RssEntry();
                rssEntry.uri = new URI(entry.getLink());
                rssEntry.category = category;
                rssEntry.subCategory = subCategory;
                rssEntry.nickname = nickName;
                rssEntries.add(rssEntry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void preStart() throws Exception {
        super.preStart();
        httpActors = Factory.getSystem().actorOf(new RoundRobinPool(5).props(Props.create(HttpActor.class)), "httpclient");
        System.out.println("Started http-round-robin-pool");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            switch (message.toString()) {
                case "harvest":
                    System.out.println("Starting to harvest");
                    if (numberOfUrlsToDownload > 0) {
                        getSender().tell(new String("In progress"), getSelf());
                    } else {
                        numberOfUrlsToDownload = rssEntries.size();
                        addAllRss();
                        for (RssEntry e: rssEntries) {
                            System.out.println("Message to download: " + e.uri.toString());
                            httpActors.tell(e, getSelf());
                        }
                        getSender().tell(new String("Started"), getSelf());
                    }
                    break;
                case "started":
                    numberOfActiveHttp++;
                    System.out.println("Started: Number of active http-agents: " + numberOfActiveHttp);
                    break;
                case "ended":
                    numberOfUrlsToDownload--;
                    numberOfActiveHttp--;
                    System.out.println("Ended: Number of active http-agents: " + numberOfActiveHttp);
                    break;
            }
        }
    }
}
