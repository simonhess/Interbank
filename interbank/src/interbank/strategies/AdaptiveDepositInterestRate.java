/**
 * 
 */
package interbank.strategies;

import java.nio.ByteBuffer;
import java.util.List;

import interbank.StaticValues;
import interbank.agents.Bank;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.InterestBearingItem;
import jmab.stockmatrix.Item;
import jmab.strategies.InterestRateStrategy;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This class lets bank set their deposit interest rate 
 * Factors influencing this rate are: the previous rate, the rate of the competitors, 
 * the rates of alternative funding, and finally the 
 * rate of profitable investments for which it can use deposits.
 */
public class AdaptiveDepositInterestRate extends AbstractStrategy implements InterestRateStrategy {

	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution; 
	private boolean increase;
	private int[] liabilitiesId;
	private double markup;
	private int mktId;
	
	/* 
	 * Main method used to compute the deposit interest rate
	 */
	@Override
	public double computeInterestRate(MacroAgent creditDemander, double amount, int length) {
		double threshold=0;
		double avInterest=0;
		SimulationController controller = (SimulationController)this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(StaticValues.BANKS_ID);
		double tot=0;
		double inter=0;
		for (Agent b:banks.getAgents()){
			Bank bank = (Bank) b;
			tot+=bank.getLiquidityRatio();
			inter+=bank.getPassedValue(StaticValues.LAG_DEPOSITINTEREST, 1);
			}
		threshold=tot/banks.getSize();
		avInterest=inter/banks.getSize();

		Bank lender=(Bank) this.getAgent();
		// first get the interest rate in the previous period
		double previousDepositRate = lender.getDepositInterestRate();
		// determine the liquidity mark-up by comparing the liquidity ratio with the target liquidity ratio
		double liquidityMarkUp = 0;
		double liquidityRatio=lender.getLiquidityRatio();
		double targetLiquidityRatio=lender.getTargetedLiquidityRatio();
		liquidityMarkUp = ((liquidityRatio-targetLiquidityRatio)/targetLiquidityRatio)+(adaptiveParameter*previousDepositRate*distribution.nextDouble());
		// determine the funding mark-up
		double interestPay=0;
		double totValue=0;
		for(int liabilityId:liabilitiesId){
			List<Item> liabilities = lender.getItemsStockMatrix(false, liabilityId);
			for(Item item:liabilities){
				InterestBearingItem liability = (InterestBearingItem) item;
				interestPay += liability.getInterestRate()*liability.getValue();
				totValue +=liability.getValue();
			}
		}
		double fundingMarkUp=0;
		double fundingRate = interestPay/totValue; //TODO change to reflect change in funding rate
		// profit mark-up
		double profitabilityMarkUp=0;
		
		// the deposit rate = previous deposit rate + liquidity mark-up + funding-mark-up + profit-mark-up
		double iR = previousDepositRate + liquidityMarkUp + fundingMarkUp + profitabilityMarkUp; 
		return Math.min(Math.max(iR, lender.getInterestRateLowerBound(mktId)),lender.getInterestRateUpperBound(mktId));
	}

	/** 
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][avInterest][mktId][increase]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(21);
		buf.putDouble(adaptiveParameter);
		buf.putInt(mktId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][avInterest][mktId][increase]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.adaptiveParameter = buf.getDouble();
		this.mktId = buf.getInt();
	}

}
