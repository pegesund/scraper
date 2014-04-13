package com.sannsyn.app.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.sannsyn.app.models.RssEntry;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.net.URI;

/**
 * Created by petter on 12.04.14.
 */
public class HttpActor extends UntypedActor{


    private void saveRss(RssEntry rssEntry) {
        // save to mongodb
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof RssEntry) {
            final RssEntry rssEntry = (RssEntry) message;
            URI uri = new URI(rssEntry.url);

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            final ActorRef sender = getSender();
            System.out.println("Starting to download: " + uri.toURL().toString());
            sender.tell(new String("started"), getSelf());
            asyncHttpClient.prepareGet(uri.toURL().toString()).execute(new AsyncCompletionHandler<Response>() {

                @Override
                public Response onCompleted(Response response) throws Exception {
                    System.out.println("-- Downloaded: " + response.getUri());
                    saveRss(rssEntry);
                    sender.tell(new String("ended"), getSender());
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    // Something wrong happened.
                }
            });
        } else
            unhandled(message);
    }
}