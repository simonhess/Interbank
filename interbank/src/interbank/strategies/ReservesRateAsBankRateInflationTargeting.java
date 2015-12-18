/**
 * 
 */
package interbank.strategies;

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
	
	/* 
	 * Main method used to compute the central bank ReservesRate()
	 */
	@Override
	public double computeReservesRate() {
		// 1. calculate inflation
		double inflation = calculateInflation(null); //TODO what argument to add to incorporate the macro simulation?
		// get the inflation target
		
		// then adjust the reserves rate depending on how far it is above or below target
		
		return 0;
	}
	
	/*
	 * Helper function used to calculate inflation
	 */
	public double calculateInflation (MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(priceIndexProducerId);
		double totalSales=0;
		double averagePrice=0;
		for (Agent a:pop.getAgents()){
			AbstractFirm firm= (AbstractFirm) a;
			totalSales+=firm.getPassedValue(realSaleId, 0);
			AbstractGood good = (AbstractGood)firm.getItemStockMatrix(true, priceGoodId);
			averagePrice+=good.getPrice()*firm.getPassedValue(realSaleId,0);
		}
		double inflation = averagePrice/totalSales;
		return inflation;
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
