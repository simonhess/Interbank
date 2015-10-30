/**
 * 
 */
package interbank.strategies;

import java.util.List;

import interbank.StaticValues;
import interbank.agents.Bank;
import interbank.agents.CentralBank;
import jmab.agents.BondSupplier;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

import java.util.Random;

/**
 * @author joeri Schasfoort
 * This strategy lets the 
 */
public class QEFromRandom extends AbstractStrategy implements
		QEStrategy {

	private int bondId;
	private int assetDemand;
	
	
	/* 
	 * Fixed asset demand quantity to be determined in xml
	 */
	@Override
	public int assetDemand() {
		return assetDemand;
	}

	/* (non-Javadoc)
	 * @see interbank.strategies.QEStrategy#bondDemand(jmab.agents.BondSupplier)
	 */
	@Override
	public int bondDemand(BondSupplier supplier) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see interbank.strategies.QEStrategy#QEPurchase()
	 */
	@Override
	public void QEPurchase() {
		// cast the central bank that will use this method
		CentralBank centralBank = (CentralBank) getAgent();
		// determine the total amount of spending available
		int assetDemand = centralBank.getQEAssetDemand();
		// get the broader population of banks
		Population banks = ((MacroPopulation)((SimulationController)this.scheduler).getPopulation()).getPopulation(StaticValues.BANKS_ID);
		// loop over the banks list and start buying bonds from these banks 1 at a time till demand is empty or there are no bonds left to buy
		while (assetDemand > 0) {
			// pick a random bank
			Bank seller = (Bank) banks.getRandomAgent();
			// get a bond, is this random??, from the bank
			List<Item> bonds = seller.getItemsStockMatrix(true, bondId);
			if (bonds.size() > 0) {
				// get a random bond
				Item buyableBond = bonds.get((int) (Math.random() * bonds.size()));
				// transaction increase bonds and decrease bonds seller bank
				centralBank.addItemStockMatrix(buyableBond, true, StaticValues.SM_BONDS);
				seller.removeItemStockMatrix(buyableBond, true, StaticValues.SM_BONDS);
				// set bond asset holder central bank
				buyableBond.setAssetHolder(centralBank);
				// increase the selling bank reserves at the central bank
				Item sellerRes= (Item) seller.getItemStockMatrix(true, StaticValues.SM_RESERVES);
				sellerRes.setValue(sellerRes.getValue()+ buyableBond.getValue());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		// TODO Auto-generated method stub

	}



}
