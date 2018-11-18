package auctionsniper;

import auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.EventListener;

public class SniperPortfolio implements SniperCollector {
	private final Announcer<PortfolioListener> announcer = Announcer.to(PortfolioListener.class);
	private final ArrayList<AuctionSniper> snipers = new ArrayList<>();

	public void addSniper(AuctionSniper sniper) {
		snipers.add(sniper);
		announcer.announce().sniperAdded(sniper);
	}

	public void addPortfolioListener(PortfolioListener listener) {
		announcer.addListener(listener);
	}

	public interface PortfolioListener extends EventListener {
		void sniperAdded(AuctionSniper sniper);
	}
}
