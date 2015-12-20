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
public class CounterCyclicalCapitalBufferCreditGDP extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private int priceIndexProducerId;//This is the population id of agents that produce the goods entering in the CPI
	private int[] gdpPopulationIds;//These are all the populations ids of agents that have either bought or produced goods entering in GDP
	private int[] gdpGoodsIds;//These are all the stock matrix ids of goods that enter in GDP
	private int[] gdpGoodsAges;//These are all age limit of goods that enter in GDP
	private LinkedHashMap<Integer,Integer> goodPassedValueMap;
	private int governmentPopulationId; // the id of the government
	private int banksPopulationId; // the id of the banks
	
	/* 
	 * Main method used to calculate the capital buffer
	 */
	@Override
	public double computePolicyTarget() {
		// First calculate the credit to GDP ratio
		double nominalGDP = calculateNominalGDP(null); // TODO argument? 
		double totalCredit = calculateTotalCredit(null); // TODO how to I reference this function?
		double creditToGDP=totalCredit/nominalGDP;
		// Then ask for the target credit to GDP ratio
		CentralBank agent= (CentralBank) this.getAgent();
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
	
	/*
	 * Helper function defined to calculate total credit
	 */
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		// first select the banks
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(banksPopulationId);
		// get a map that includes all credit? 
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		List<String> bsNames = ((SimpleAbstractAgent)pop.getAgentList().get(0)).getStocksNames();
		double[][] aggBs = new double[2][bsNames.size()];
		for (Agent a:pop.getAgents()){
			MacroAgent agent=(MacroAgent) a;
			double[][] bs = agent.getNumericBalanceSheet();
			for (int i = 0 ; i<bsNames.size() ; i++){
				aggBs[0][i]+=bs[0][i];
				aggBs[1][i]+=bs[1][i];
			}
		}
		for (int i = 0 ; i<bsNames.size() ; i++){
			result.put((long) i, aggBs[0][i]-aggBs[1][i]);
		}
		return result;
	}
	
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


}
