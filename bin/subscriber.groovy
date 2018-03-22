import org.cometd.bayeux.Channel
import org.cometd.bayeux.Message
import org.cometd.bayeux.client.ClientSessionChannel
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener
import org.cometd.client.BayeuxClient
import org.cometd.client.transport.ClientTransport
import org.cometd.client.transport.LongPollingTransport
import org.eclipse.jetty.client.HttpClient as MyHttpClient
//import org.json.JSONObject
//import org.cometd.bayeux.server.LocalSession
//import java.util.concurrent.CountDownLatch;
//import java.util.Collections

String myurl
String channelname

try{
    myurl = args[0]
    channelname = args[1]
}
catch (e)
{
    println '''Usage : ./comet url channel
Example :

./comet publisher.py http://192.168.11.170:8080/cometd /members/hello
'''
    System.exit(0)
}


//-------------------: Listener

ClientSessionChannel.MessageListener mylistener = new Mylistener();

ClientSessionChannel.MessageListener cblistener = new MyCallbacklistener ();

//--------------------: httpclient

MyHttpClient httpClient = new MyHttpClient();

httpClient.start()

//--------------------: setup client

Map<String, Object> options = new HashMap<String, Object>();

ClientTransport transport = new LongPollingTransport(options, httpClient)        

BayeuxClient client = new BayeuxClient(myurl, transport)

println 'client started on URL : '+ client.getURL()

client.handshake(mylistener)


//--------------------: handshakecheck

boolean handshakecheck = client.waitFor(5000, BayeuxClient.State.CONNECTED);

println 'Handshake check : '+ handshakecheck

if (handshakecheck)
{
    def mychannel = client.getChannel(channelname)

    mychannel.subscribe (mylistener);

    while (true)

        Thread.sleep(15000);

    client.waitFor(1000, BayeuxClient.State.DISCONNECTED)

    println ' program ended ------------------------'

}
/*
client.batch(new Runnable() {
@Override
public void run() {
  mychannel.subscribe(new ClientSessionChannel.MessageListener() {
      public void onMessage(ClientSessionChannel channel, Message message) 
      {
        println "this is new :" + message + channel
      
      }}, cblistener);
      //Thread.sleep (1000)
      }}
      //mychannel.publish('$$$$$$$$  cometd working $$$$$$$$',cblistener);
*/
//mychannel.unsubscribe(mylistener)
//client.disconnect(mylistener)
//--------------------: class

class Mylistener implements ClientSessionChannel.MessageListener {

        public void onMessage(ClientSessionChannel channel, Message message) {

            println message.data
        }

}

class MyCallbacklistener implements ClientSessionChannel.MessageListener {

        public void onMessage(ClientSessionChannel channel, Message message) {

            println 'Call back :' + message + '-----' + channel
        }

}