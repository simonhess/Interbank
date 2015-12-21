/**
 * 
 */
package interbank.strategies;

import interbank.StaticValues;
import interbank.agents.CentralBank;

import java.util.LinkedHashMap;
import java.util.List;

import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.goods.AbstractGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

	

/**
 * @author Joeri Schasfoort set time varying counter cyclical reserve requirements
 * based on a credit to GDP ratio. If this ratio gets higher the reserve requirements 
 * are increased up to a maxiumum of 2.5% on the base level
 */
public class TimeVaryingReserveRequirements extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private int priceIndexProducerId;//This is the population id of agents that produce the goods entering in the CPI
	private int[] gdpPopulationIds;//These are all the populations ids of agents that have either bought or produced goods entering in GDP
	private int[] gdpGoodsIds;//These are all the stock matrix ids of goods that enter in GDP
	private int[] gdpGoodsAges;//These are all age limit of goods that enter in GDP
	private LinkedHashMap<Integer,Integer> goodPassedValueMap;
	private int governmentPopulationId; // the id of the government
	private int banksPopulationId; // the id of the banks
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
	
	/**
	 * 
	 */
	public TimeVaryingReserveRequirements() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param agent
	 */
	public TimeVaryingReserveRequirements(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public TimeVaryingReserveRequirements(EventScheduler scheduler, Agent agent) {
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
