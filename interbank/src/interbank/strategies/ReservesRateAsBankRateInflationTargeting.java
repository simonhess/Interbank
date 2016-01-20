/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;
import jmab.population.MacroPopulation;
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
	 * @return the lagInflation
	 */
	public int getLagInflation() {
		return lagInflation;
	}

	/**
	 * @param lagInflation the lagInflation to set
	 */
	public void setLagInflation(int lagInflation) {
		this.lagInflation = lagInflation;
	}

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
