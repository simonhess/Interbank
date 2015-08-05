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
 * This class lets the policy maker set a counter cyclical capital buffer
 * based on a credit to GDP ratio. If this ratio gets higher the capital buffer 
 * is increased up to a maxiumum of 2.5% on the base level, in line with Basel III 
 */
public class CounterCyclicalCapitalBufferCreditGDP extends AbstractStrategy implements
		MacroPrudentialStrategy {

	/* (non-Javadoc)
	 * Main method used to calculate the capital buffer
	 */
	@Override
	public double computePolicyTarget() {
		// First calculate the credit to GDP ratio
		
		// then determine the markup on the capital buffer
		return 0;
	}
	
	/**
	 * 
	 */
	public CounterCyclicalCapitalBufferCreditGDP() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param agent
	 */
	public CounterCyclicalCapitalBufferCreditGDP(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public CounterCyclicalCapitalBufferCreditGDP(EventScheduler scheduler, Agent agent) {
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


}
