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
 * This strategy lets the central bank set the reserve rate as its main monetary policy tool
 * This is modelled after the Bank of England. The CB increases the interest rate by a small amount
 * if inflation is above target and decreases it if it is below target. 
 */
public class ReservesRateAsBankRateInflationTargeting extends AbstractStrategy
		implements ReservesRateStrategy {

	/**
	 * 
	 */
	public ReservesRateAsBankRateInflationTargeting() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param agent
	 */
	public ReservesRateAsBankRateInflationTargeting(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public ReservesRateAsBankRateInflationTargeting(EventScheduler scheduler,
			Agent agent) {
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
	 * @see interbank.strategies.ReservesRateStrategy#computeReservesRate()
	 */
	@Override
	public double computeReservesRate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
