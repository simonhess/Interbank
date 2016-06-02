/**
 * 
 */
package interbank.strategies;

import java.util.List;

import interbank.agents.Bank;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Deposit;
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import jmab.strategies.InterestRateStrategy;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author joerischasfoort
 * Using this strategy banks set their interbank ask as a risk adjusted mark-up for stashing their 
 * liquidity at the central bank.
 */
public class InterbankAskCBMarkUp extends AbstractStrategy implements InterestRateStrategy {

	private int reserveId;
	private int mktId;
	
	/* 
	 * Main method used to compute the interest rate as a mark-up of the CB reserve deposit rate
	 */
	@Override
	public double computeInterestRate(MacroAgent creditDemander, double amount, int length) {
		Bank bank = (Bank)this.agent;
		Deposit reserve = (Deposit)bank.getItemStockMatrix(true, reserveId);
		double centralBankDepositRate = reserve.getInterestRate();
		double interBankRiskPremium = bank.getInterBankRiskPremium();
		return Math.min(Math.max(centralBankDepositRate*interBankRiskPremium, bank.getInterestRateLowerBound(mktId)),bank.getInterestRateUpperBound(mktId));
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
