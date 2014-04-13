package com.sannsyn.app;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import akka.util.Timeout;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.sannsyn.app.akka.HttpActor;
import com.sannsyn.app.akka.RssActor;
import com.sannsyn.app.models.Ptest;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import static akka.pattern.Patterns.ask;
import scala.concurrent.Await;
import akka.util.Timeout;
import scala.concurrent.duration.*;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static akka.pattern.Patterns.gracefulStop;
import static com.sannsyn.app.Factory.*;

import scala.concurrent.Future;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void getConnection() throws Exception {
        DB db = new MongoClient().getDB("scraper");
        Jongo jongo = new Jongo(db);
        System.out.println("Jongo: " + jongo);
        System.out.println("I am saved");

    }

    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Starting akka" );
        startAkka();
        ActorRef rss = getSystem().actorOf(Props.create(RssActor.class), "rss");
        System.out.println("Rss actor started");
        MongoCollection links = Factory.getJongo().getCollection("ptest");
        Ptest ptest = new Ptest();
        ptest.nick = "test";
        ptest.names.add("xx");
        ptest.names.add("yy");
        links.insert(ptest);

        /*
        Timeout timeout = new Timeout(Duration.create(1, "seconds"));
        Future<Object> res = ask(rss, "harvest", 10000);
        try {
            String result = (String) Await.result(res, timeout.duration());
            System.out.println("Got result: " + result);
        } catch (TimeoutException e) {
            System.out.println("Oh no, it timed out");
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        Cancellable cancellable = getSystem().scheduler().schedule(Duration.Zero(),
                Duration.create(5, TimeUnit.SECONDS), rss, "harvest",
                getSystem().dispatcher(), getSystem().lookupRoot());
    }


}
