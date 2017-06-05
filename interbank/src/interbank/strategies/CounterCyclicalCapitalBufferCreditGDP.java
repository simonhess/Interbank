/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This class lets the policy maker set a counter cyclical capital buffer
 * based on a credit to GDP ratio. If this ratio gets higher the capital buffer 
 * is increased up to a maxiumum of 2.5% on the base level, in line with Basel III 
 */
@SuppressWarnings("serial")
public class CounterCyclicalCapitalBufferCreditGDP extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private int lagNominalGDP;
	private int lagTotalCredit;
	/* 
	 * Main method used to calculate the capital buffer
	 */
	@Override
	public double computePolicyTarget() {
		CentralBank cb= (CentralBank) this.getAgent();
		// First calculate the credit to GDP ratio
		double nominalGDP = cb.getAggregateValue(lagNominalGDP, 1);  
		double totalCredit = cb.getAggregateValue(lagTotalCredit, 1);
		double creditToGDP=totalCredit/nominalGDP;
		// Then ask for the target credit to GDP ratio
		double targetCreditToGDP = cb.getTargetCreditToGDP();
		// ask for the current capital requirements, threshold, and policy markup
		double currentCAR = cb.getCAR();
		double prudentialThreshold = cb.getPrudentialThreshold();
		double prudentialMarkUp = cb.getPrudentialMarkUp();
		double maxCAR = cb.getMaxCAR();
		double minCAR = cb.getMinCAR();
		// then adjust the CAR depending on how far it is above or below target
		double CreditOfTarget = (creditToGDP - targetCreditToGDP)/targetCreditToGDP;
		double newCAR = currentCAR;
		if (CreditOfTarget > prudentialThreshold) {
			newCAR = Math.min(currentCAR + prudentialMarkUp,maxCAR);
		}
		else if (CreditOfTarget < -prudentialThreshold) {
			newCAR = Math.max(currentCAR - prudentialMarkUp,minCAR);
		}
		return newCAR;
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

	/**
	 * @return the lagNominalGDP
	 */
	public int getLagNominalGDP() {
		return lagNominalGDP;
	}

	/**
	 * @param lagNominalGDP the lagNominalGDP to set
	 */
	public void setLagNominalGDP(int lagNominalGDP) {
		this.lagNominalGDP = lagNominalGDP;
	}

	/**
	 * @return the lagTotalCredit
	 */
	public int getLagTotalCredit() {
		return lagTotalCredit;
	}

	/**
	 * @param lagTotalCredit the lagTotalCredit to set
	 */
	public void setLagTotalCredit(int lagTotalCredit) {
		this.lagTotalCredit = lagTotalCredit;
	}

	
	
}
