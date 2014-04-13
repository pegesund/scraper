package com.sannsyn.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import scala.concurrent.Await;
import akka.util.Timeout;
import scala.concurrent.duration.*;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static akka.pattern.Patterns.gracefulStop;

import scala.concurrent.Future;


/**
 * Created by petter on 12.04.14.
 */
public class Factory {
    static ActorSystem system;
    static DB db;
    static Jongo jongo;

    static public ActorSystem getSystem(){
        return system;
    }

    public static void startAkka() {
        system = ActorSystem.create("MySystem");
        System.out.println("Akka-system started");
        // final ActorRef myActor = system.actorOf(Props.create(PActor.class), "myactor");
    }

    public static synchronized Jongo getJongo() throws UnknownHostException {
        if (db == null) {
            db = new MongoClient().getDB("scraper");
            jongo = new Jongo(db);
        }
        return jongo;
    }


}
