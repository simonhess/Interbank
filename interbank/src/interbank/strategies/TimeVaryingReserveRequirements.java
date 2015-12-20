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
	
	/* 
	 * Main method used to calculate the required reserves
	 */
	@Override
	public double computePolicyTarget() {
		// First calculate the credit to GDP ratio
		double nominalGDP = calculateNominalGDP(null); // TODO argument? 
		double totalCredit = calculateTotalCredit(null);
		double creditToGDP=totalCredit/nominalGDP;
		// Then ask for the target credit to GDP ratio
		CentralBank agent= (CentralBank) this.getAgent();
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
	
	// TODO create helper function to calculate total credit
	
	/*
	 * Helper function defined to calculate nominal GDP 
	 */
	public double calculateNominalGDP(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(priceIndexProducerId);
		double gdpGoodsComponent=0;
			double pastInventories=0;
			double publicServantsWages=0;
			double nominalGDP=0;
			for(int popId:gdpPopulationIds){
				pop = macroPop.getPopulation(popId);
				//Population pop = macroPop.getPopulation(i); GET RID OF THIS?
				for(Agent j:pop.getAgents()){
					MacroAgent agent=(MacroAgent) j;
					for(int k=0; k<gdpGoodsIds.length;k++){
						List<Item> items= agent.getItemsStockMatrix(true, gdpGoodsIds[k]);
						for(Item item:items){
							if(item.getAge()<gdpGoodsAges[k]){
								gdpGoodsComponent+=item.getValue();
							}
							AbstractGood good = (AbstractGood)item;
							if(good.getProducer().getAgentId()==agent.getAgentId()){
								int passedValueId = goodPassedValueMap.get(good.getSMId());
								pastInventories+=agent.getPassedValue(passedValueId, 1);
							}
						}
					}					
				}
				gdpGoodsComponent-=pastInventories;
				if(governmentPopulationId!=-1){
					LaborDemander govt = (LaborDemander)macroPop.getPopulation(governmentPopulationId).getAgentList().get(0);
					for(MacroAgent agent:govt.getEmployees()){
						LaborSupplier publicServant = (LaborSupplier)agent;
						publicServantsWages+=publicServant.getWage();
					}
					nominalGDP = gdpGoodsComponent+publicServantsWages;
				}else
					nominalGDP = gdpGoodsComponent;
			}
			return nominalGDP;
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
