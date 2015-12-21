/**
 * 
 */
package interbank.strategies;

import interbank.agents.CentralBank;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.agents.SimpleAbstractAgent;
import jmab.goods.AbstractGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
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

	/**
	 * @return the priceIndexProducerId
	 */
	public int getPriceIndexProducerId() {
		return priceIndexProducerId;
	}

	/**
	 * @param priceIndexProducerId the priceIndexProducerId to set
	 */
	public void setPriceIndexProducerId(int priceIndexProducerId) {
		this.priceIndexProducerId = priceIndexProducerId;
	}

	/**
	 * @return the gdpPopulationIds
	 */
	public int[] getGdpPopulationIds() {
		return gdpPopulationIds;
	}

	/**
	 * @param gdpPopulationIds the gdpPopulationIds to set
	 */
	public void setGdpPopulationIds(int[] gdpPopulationIds) {
		this.gdpPopulationIds = gdpPopulationIds;
	}

	/**
	 * @return the gdpGoodsIds
	 */
	public int[] getGdpGoodsIds() {
		return gdpGoodsIds;
	}

	/**
	 * @param gdpGoodsIds the gdpGoodsIds to set
	 */
	public void setGdpGoodsIds(int[] gdpGoodsIds) {
		this.gdpGoodsIds = gdpGoodsIds;
	}

	/**
	 * @return the gdpGoodsAges
	 */
	public int[] getGdpGoodsAges() {
		return gdpGoodsAges;
	}

	/**
	 * @param gdpGoodsAges the gdpGoodsAges to set
	 */
	public void setGdpGoodsAges(int[] gdpGoodsAges) {
		this.gdpGoodsAges = gdpGoodsAges;
	}

	/**
	 * @return the goodPassedValueMap
	 */
	public LinkedHashMap<Integer, Integer> getGoodPassedValueMap() {
		return goodPassedValueMap;
	}

	/**
	 * @param goodPassedValueMap the goodPassedValueMap to set
	 */
	public void setGoodPassedValueMap(
			LinkedHashMap<Integer, Integer> goodPassedValueMap) {
		this.goodPassedValueMap = goodPassedValueMap;
	}

	/**
	 * @return the governmentPopulationId
	 */
	public int getGovernmentPopulationId() {
		return governmentPopulationId;
	}

	/**
	 * @param governmentPopulationId the governmentPopulationId to set
	 */
	public void setGovernmentPopulationId(int governmentPopulationId) {
		this.governmentPopulationId = governmentPopulationId;
	}

	/**
	 * @return the banksPopulationId
	 */
	public int getBanksPopulationId() {
		return banksPopulationId;
	}

	/**
	 * @param banksPopulationId the banksPopulationId to set
	 */
	public void setBanksPopulationId(int banksPopulationId) {
		this.banksPopulationId = banksPopulationId;
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
