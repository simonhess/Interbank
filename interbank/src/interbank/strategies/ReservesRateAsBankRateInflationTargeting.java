/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;
import jmab.agents.AbstractFirm;
import jmab.goods.AbstractGood;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
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

	private int priceIndexProducerId;//This is the population id of agents that produce the goods entering in the CPI
	private int realSaleId;//This is the id of the lagged value of real sales
	private int priceGoodId;//This is the stock matrix if of the good entering in the CPI
	private int lagInflation;
	
	/* 
	 * Main method used to compute the central bank ReservesRate()
	 */
	@Override
	public double computeReservesRate() {
		// cast the central bank as the asking agent
		CentralBank agent= (CentralBank) this.getAgent();
		// get the inflation, inflation target, current interest rate, monetary policy markup, and threshold
		double inflation = agent.getAggregateValue(lagInflation, 1);
		double targetInflation = agent.getTargetInflation();
		double currentBankRate = agent.getReserveInterestRate();
		double monetaryThreshold = agent.getMonetaryThreshold();
		double monetaryPolicyMarkUp = agent.getMonetaryPolicyMarkUp();
		// then adjust the reserves rate depending on how far it is above or below target
		double inflationOfTarget = inflation - targetInflation;
		double newBankRate;
		if (inflationOfTarget > monetaryThreshold) {
			newBankRate = currentBankRate + monetaryPolicyMarkUp;
		}
		if (inflationOfTarget < monetaryThreshold) {
			newBankRate = currentBankRate - monetaryPolicyMarkUp;
		}
		else {newBankRate = currentBankRate;}
		return newBankRate;
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
