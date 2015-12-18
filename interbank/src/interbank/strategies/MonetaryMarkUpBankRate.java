/**
 * 
 */
package interbank.strategies;

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

	/**
	 * 
	 */
	public MonetaryMarkUpBankRate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param agent
	 */
	public MonetaryMarkUpBankRate(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public MonetaryMarkUpBankRate(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
		// TODO Auto-generated constructor stub
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

	/* (non-Javadoc)
	 * @see interbank.strategies.MonetaryPolicyStrategy#computeAdvancesRate()
	 */
	@Override
	public double computeAdvancesRate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
