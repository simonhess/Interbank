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
		CentralBank agent= (CentralBank) this.getAgent();
		// First calculate the credit to GDP ratio
		double nominalGDP = agent.getAggregateValue(lagNominalGDP, 1);  
		double totalCredit = agent.getAggregateValue(lagTotalCredit, 1);
		double creditToGDP=totalCredit/nominalGDP;
		// Then ask for the target credit to GDP ratio
		double targetCreditToGDP = agent.getTargetCreditToGDP();
		// ask for the current capital requirements, threshold, and policy markup
		double currentCAR = agent.getCAR();
		double prudentialThreshold = agent.getPrudentialThreshold();
		double prudentialMarkUp = agent.getPrudentialMarkUp();
		// then adjust the CAR depending on how far it is above or below target
		double CreditOfTarget = creditToGDP - targetCreditToGDP;
		double newCAR;
		if (CreditOfTarget > prudentialThreshold) {
			newCAR = currentCAR + prudentialMarkUp;
		}
		if (CreditOfTarget < prudentialThreshold) {
			newCAR = currentCAR - prudentialMarkUp;
		}
		else {newCAR = currentCAR;}
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
