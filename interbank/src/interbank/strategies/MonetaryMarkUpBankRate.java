/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This strategy lets the central bank set the advances rate at a fixed mark-up 
 * of the reserve bank rate. This is modelled after the Bank of England.
 */
public class MonetaryMarkUpBankRate extends AbstractStrategy implements
		MonetaryPolicyStrategy {

	/* (non-Javadoc)
	 * Main method used to set the advances rate
	 */
	@Override
	public double computeAdvancesRate() {
		// get the reserve deposit rate and mark-up
		CentralBank agent= (CentralBank) this.getAgent();
		double bankRate = agent.getReserveInterestRate();
		double markUp = agent.getMonetaryMarkUp();
		// apply mark-up to deposit rate
		double AdvancesRate = bankRate + markUp;
		return 0;
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
