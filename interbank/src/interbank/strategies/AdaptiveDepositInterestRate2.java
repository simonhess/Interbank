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
public class AdaptiveDepositInterestRate2 extends AbstractStrategy implements InterestRateStrategy {

	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution; 
	private int[] liabilitiesId;
	private int mktId;
	private double spread;
	
	/* 
	 * Main method used to compute the deposit interest rate
	 */
	@Override
	public double computeInterestRate(MacroAgent creditDemander, double amount, int length) {
		double avInterest=0;
		SimulationController controller = (SimulationController)this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(StaticValues.BANKS_ID);
		double inter=0;
		for (Agent b:banks.getAgents()){
			Bank bank = (Bank) b;
			inter+=bank.getPassedValue(StaticValues.LAG_DEPOSITINTEREST, 1);
			}
		avInterest=inter/banks.getSize();
		Bank lender=(Bank) this.getAgent();
		// determine the liquidity position by comparing the liquidity ratio with the target liquidity ratio
		double liquidityRatio=lender.getLiquidityRatio();
		double targetLiquidityRatio=lender.getTargetedLiquidityRatio();
		double iR=0;
	    
		double lendingRate = lender.getBankInterestRate();
		double spread = this.spread;
	    if(lendingRate-spread>avInterest){
			spread-=spread*adaptiveParameter*distribution.nextDouble();
		}else{
			spread+=spread*adaptiveParameter*distribution.nextDouble();
		}
	    if(liquidityRatio>targetLiquidityRatio){
			spread-=spread*adaptiveParameter*distribution.nextDouble();
		}else{
			spread+=spread*adaptiveParameter*distribution.nextDouble();
		}
	    if (spread < 0) spread = 0;
	    
		iR = lendingRate-spread;
	    double finalRate = Math.min(Math.max(iR, lender.getInterestRateLowerBound(mktId)),lender.getInterestRateUpperBound(mktId));
		return finalRate;
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

	public double getAdaptiveParameter() {
		return adaptiveParameter;
	}

	public void setAdaptiveParameter(double adaptiveParameter) {
		this.adaptiveParameter = adaptiveParameter;
	}

	public double getSpread() {
		return spread;
	}

	public void setSpread(double spread) {
		this.spread = spread;
	}

	public AbstractDelegatedDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(AbstractDelegatedDistribution distribution) {
		this.distribution = distribution;
	}

	public int[] getLiabilitiesId() {
		return liabilitiesId;
	}

	public void setLiabilitiesId(int[] liabilitiesId) {
		this.liabilitiesId = liabilitiesId;
	}

	public int getMktId() {
		return mktId;
	}

	public void setMktId(int mktId) {
		this.mktId = mktId;
	}

}
