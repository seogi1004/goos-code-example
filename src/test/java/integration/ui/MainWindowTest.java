package integration.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener.Item;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import endtoend.AuctionSniperDriver;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
	static {
		System.setProperty("com.objogate.wl.keyboard", "US");
	}

	private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "item request");
		mainWindow.addUserRequestListener(itemProbe::setReceivedValue);

		driver.startBiddingWithStopPrice("an item-id", 789);
		driver.check(itemProbe);
	}
}
