///@Grab(group='org.cometd.java', module='cometd-java-client', version='3.1.3')

//import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.Message.Mutable
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient
import org.cometd.client.transport.ClientTransport
import org.cometd.client.transport.LongPollingTransport
import org.eclipse.jetty.client.HttpClient as MyHttpClient

//import org.json.JSONObject
//import org.cometd.bayeux.server.LocalSession


//-------------------: Listener


 ClientSessionChannel.MessageListener mylistener = new Mylistener();



//--------------------: url

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

//--------------------: httpclient 

MyHttpClient httpClient = new MyHttpClient();

httpClient.start()

println 'Step 1 : httpClient started'

//--------------------: setup client

Map<String, Object> options = new HashMap<String, Object>();

ClientTransport transport = new LongPollingTransport(options, httpClient);

BayeuxClient client = new BayeuxClient(myurl, transport)

println 'client started on URL : '+ client.getURL()


//--------------------: Handshake Listener
/*

client.handshake ( new ClientSessionChannel.MessageListener() {

    public void onMessage(ClientSessionChannel channel, Message message) {

        if (message.isSuccessful()) {
        	
        	println 'Handshake completed ....'

        	println 'Handshake Message : ' + message

        }
    }
})

*/
client.handshake(mylistener)
//--------------------: handshakecheck

boolean handshakecheck = client.waitFor(1000, BayeuxClient.State.CONNECTED);

println 'Handshake check : '+ handshakecheck

//---------------------: Batch

if (handshakecheck)
{
    def channel = client.getChannel(channelname)
    //channel.subscribe(mylistener)
    // Sleep time needed for protocol data transaction .
    Thread.sleep(2000)
    int a = 0
    while (true)
    {
        sleep(2000)
        a++
        channel.publish(["$a": new Date()] ,mylistener)
    }
}

//--------------------: class

class Mylistener implements ClientSessionChannel.MessageListener {

        public void onMessage(ClientSessionChannel channel, Message message) {

            println message

        }

}