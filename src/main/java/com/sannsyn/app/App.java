package com.sannsyn.app;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import akka.util.Timeout;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.sannsyn.app.akka.HttpActor;
import com.sannsyn.app.akka.RssActor;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.jongo.Jongo;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
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
        Factory.startAkka();
        ActorRef rss = Factory.getSystem().actorOf(Props.create(RssActor.class), "rss");
        System.out.println("Rss actor started");
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
    }


}
