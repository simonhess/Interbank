/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

	

/**
 * @author Joeri Schasfoort set time varying counter cyclical reserve requirements
 * based on a credit to GDP ratio. If this ratio gets higher the reserve requirements 
 * are increased up to a maxiumum of 2.5% on the base level
 */
public class TimeVaryingReserveRequirements extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private int lagNominalGDP;
	private int lagTotalCredit;
	
	/* 
	 * Main method used to calculate the required reserves
	 */
	@Override
	public double computePolicyTarget() {
		// cast CB agent
		CentralBank agent= (CentralBank) this.getAgent();
		// First calculate the credit to GDP ratio
		double nominalGDP = agent.getAggregateValue(lagNominalGDP, 1);  
		double totalCredit = agent.getAggregateValue(lagTotalCredit, 1);
		double creditToGDP=totalCredit/nominalGDP;
		// Then ask for the target credit to GDP ratio
		double targetCreditToGDP = agent.getTargetCreditToGDP();
		// ask for the current reserve requirements, threshold, and policy markup
		double currentReserveRequirements = agent.getLiquidityRatio();
		double prudentialThreshold = agent.getPrudentialThreshold();
		double prudentialMarkUp = agent.getPrudentialMarkUp();
		// then adjust the LR depending on how far it is above or below target
		double CreditOfTarget = creditToGDP - targetCreditToGDP;
		double newReserveRequirements;
		if (CreditOfTarget > prudentialThreshold) {
			newReserveRequirements = currentReserveRequirements + prudentialMarkUp;
		}
		if (CreditOfTarget < prudentialThreshold) {
			newReserveRequirements = currentReserveRequirements - prudentialMarkUp;
		}
		else {newReserveRequirements = currentReserveRequirements;}
		return newReserveRequirements;
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
