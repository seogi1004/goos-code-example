package endtoend;

import auctionsniper.xmpp.XMPPAuction;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String XMPP_HOSTNAME = "antop.org";

	private static final String AUCTION_PASSWORD = "auction";
	private final SingleMessageListener messageListener = new SingleMessageListener();
	private final String itemId;
	private final XMPPConnection connection;

	private Chat currentChat;

	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD,
				AUCTION_RESOURCE);
		connection.getChatManager().addChatListener((Chat chat, boolean createdLocally) -> {
			currentChat = chat;
			chat.addMessageListener(messageListener);

		});
	}

	public void sendInvalidMessageContaining(String brokenMessage) throws XMPPException {
		currentChat.sendMessage(brokenMessage);
	}

	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; "
								+ "CurrentPrice: %d; Increment: %d; Bidder: %s;",
						price, increment, bidder));
	}

	public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(XMPPAuction.JOIN_COMMAND_FORMAT));
	}

	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId,
				equalTo(String.format(XMPPAuction.BID_COMMAND_FORMAT, bid)));
	}

	private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}

	public void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}

	public void stop() {
		connection.disconnect();
	}

	public String getItemId() {
		return itemId;
	}

	public class SingleMessageListener implements MessageListener {
		private final ArrayBlockingQueue<Message> messages =
				new ArrayBlockingQueue<>(1);

		public void processMessage(Chat chat, Message message) {
			messages.add(message);
		}

		public void receivesAMessage() throws InterruptedException {
			assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
		}

		public void receivesAMessage(Matcher<? super String> messageMatcher)
				throws InterruptedException {
			final Message message = messages.poll(5, TimeUnit.SECONDS);
			assertThat(message, hasProperty("body", messageMatcher));
		}
	}

}
