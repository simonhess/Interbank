/**
 * 
 */
package interbank.strategies;

import jmab.agents.BondSupplier;
import jmab.strategies.SingleStrategy;

/**
 * @author Joeri Schasfoort
 * 
 */
public interface QEStrategy extends SingleStrategy {
	
	public int assetDemand();
	public int bondDemand(BondSupplier supplier);

	public void QEPurchase();
}
