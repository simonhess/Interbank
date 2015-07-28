/**
 * 
 */
package interbank.strategies;

import interbank.agents.Bank;
import jmab.agents.AbstractBank;
import jmab.agents.CreditSupplier;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort & Antoine Godin
 * This strategy lets the central bank set its advances interest rate
 * adaptively using a Taylor rule 
 */
public class MonetaryTaylor extends AbstractStrategy implements
		MonetaryPolicyStrategy {
	
	private int gdpAVID;
	private int inflationAVID;
	private int inflationCoefficientId;
	private int outputCoefficientId;
	private double taylorInterestRate;
	
	public double getTaylorInterestRate() {
		return this.taylorInterestRate;
	}

	public void setTaylorInterestRate(double taylorInterestRate) {
		this.taylorInterestRate = taylorInterestRate;
	}
	
	/**
	 * compute the AdvancesRate based on the Taylor rule 
	 * advancesInterestRate = 
	 * inflation + expectedNaturalRate  
	 * + inflationCoefficient * (inflation - desiredInflation) 
	 * + outputCoefficient * (Math.log(Output) – Math.log(PotentialOutput))
	 */
	@Override
	public double computeAdvancesRate() {
		/*
		// get from macrosimulation computer???:
		// 1. inflation
		// 2. output or RealGDP
		agent= this.getAgent();
		double inflation = agent.getAggregateValue(inflationAVID, 1);
		double GDP = agent.getAggregateValue(gdpAVID, 1);
		// get from central bank 
		// 1. expectedNaturalRate
		// 2. assumedPotentialOutput
		double expectedAssumedInflation = agent.get;
		double expectedGDP = agent.getExpectation(key);
		// define the bank
		AbstractBank bank = (AbstractBank) this.agent; 
		// Compute the interest rate according to the taylor rule
		double AdvancesRate = inflation + expectedAssumedInflation + () + ();
		
		return AdvancesRate; // return the AdvancesRate
		/*/
		return 0;
		//*/
	}
	
	
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

	public int getGdpAVID() {
		return gdpAVID;
	}

	public void setGdpAVID(int gdpAVID) {
		this.gdpAVID = gdpAVID;
	}

	public int getInflationAVID() {
		return inflationAVID;
	}

	public void setInflationAVID(int inflationAVID) {
		this.inflationAVID = inflationAVID;
	}

	/* (non-Javadoc)
	 * @see interbank.strategies.MonetaryPolicyStrategy#computeAdvancesRate()
	 */


}
