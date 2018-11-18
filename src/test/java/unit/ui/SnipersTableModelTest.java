package unit.ui;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.UserRequestListener.Item;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.util.Defect;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SnipersTableModelTest {
	private static final String ITEM_ID = "item 0";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final SnipersTableModel model = new SnipersTableModel();
	private final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, 234), null);
	private TableModelListener listener = context.mock(TableModelListener.class);

	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}

	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}

	@Test
	public void setsUpColumnHeadings() {
		for (Column column : Column.values()) {
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}

	@Test
	public void acceptsNewSniper() {
		context.checking(new Expectations() {{
			oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
		}});

		model.sniperAdded(sniper);

		assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));
	}

	@Test
	public void setsSniperValuesInColumns() {
		SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anyInsertionEvent()));

			oneOf(listener).tableChanged(with(aChangeInRow(0)));
		}});

		model.sniperAdded(sniper);
		model.sniperStateChanged(bidding);

		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	public void notifiesListenersWhenAddingASniper() {
		context.checking(new Expectations() {
			{
				oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
			}
		});

		assertEquals(0, model.getRowCount());

		model.sniperAdded(sniper);

		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));
	}

	@Test
	public void holdsSnipersInAdditionOrder() {
		AuctionSniper sniper2 = new AuctionSniper(new Item("item 1", 345), null);
		context.checking(new Expectations() {
			{
				ignoring(listener);
			}
		});

		model.sniperAdded(sniper);
		model.sniperAdded(sniper2);

		assertEquals(ITEM_ID, cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}

	@Test
	public void updatesCorrectRowForSniper() {
		AuctionSniper sniper2 = new AuctionSniper(new Item("item 1", 345), null);
		context.checking(new Expectations() {
			{
				allowing(listener).tableChanged(with(anyInsertionEvent()));

				oneOf(listener).tableChanged(with(aChangeInRow(1)));
			}
		});

		model.sniperAdded(sniper);
		model.sniperAdded(sniper2);

		SniperSnapshot winning1 = sniper2.getSnapshot().winning(123);
		model.sniperStateChanged(winning1);

		assertRowMatchesSnapshot(1, winning1);
	}

	@Test(expected = Defect.class)
	public void throwsDefectIfNoExistingSniperForAnUpdate() {
		model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING));
	}

	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
		assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
		assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
		assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
	}

	private Object cellValue(int rowIndex, Column column) {
		return model.getValueAt(rowIndex, column.ordinal());
	}

	Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}

	Matcher<TableModelEvent> anInsertionAtRow(final int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
	}

} 
