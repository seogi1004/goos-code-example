package unit;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperPortfolio;
import auctionsniper.SniperPortfolio.PortfolioListener;
import auctionsniper.UserRequestListener.Item;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class SniperPortfolioTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final PortfolioListener listener = context.mock(PortfolioListener.class);
	private final SniperPortfolio portfolio = new SniperPortfolio();

	@Test
	public void notifiesListenersOfNewSnipers() {
		final AuctionSniper sniper = new AuctionSniper(new Item("item id", 123), null);
		context.checking(new Expectations() {{
			oneOf(listener).sniperAdded(sniper);
		}});
		portfolio.addPortfolioListener(listener);

		portfolio.addSniper(sniper);
	}
}
