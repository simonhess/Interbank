/**
 * 
 */
package interbank.strategies;

import interbank.StaticValues;
import interbank.agents.Bank;
import jmab.agents.CreditDemander;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.strategies.DefaultProbilityComputer;

/**
 * @author joeri
 * This class allows banks to make different assessments of projects based on their capital ratio
 */
public class DeterministicLogisticCashFlowComputerWithCapRatio implements
		DefaultProbilityComputer {
	
	private int idLoanSM;

	/* (non-Javadoc)
	 * @see jmab.strategies.DefaultProbilityComputer#getDefaultProbability(jmab.agents.MacroAgent, jmab.agents.MacroAgent)
	 */ 
	@Override
	public double getDefaultProbability(MacroAgent creditDemander,
			MacroAgent creditSupplier, double demanded) {
		double operatingCashFlow=creditDemander.getPassedValue(StaticValues.LAG_OPERATINGCASHFLOW, 1);
		Bank creditSupplier1= (Bank) creditSupplier;
		CreditDemander creditDemander1= (CreditDemander) creditDemander;
		double demandedLoanInterestPaymentPerPeriod=creditSupplier1.getInterestRate(idLoanSM, creditDemander, demanded, creditDemander1.decideLoanLength(StaticValues.SM_LOAN))*demanded;
		double demandedLoanPaymentsPerPeriod=demandedLoanInterestPaymentPerPeriod+demanded/creditDemander1.decideLoanLength(StaticValues.SM_LOAN);
		// get the banks risk aversion parameter
		double bankRiskAversion=creditSupplier1.getRiskAversion(creditDemander);
		// get the banks capital ratio and their target
		double bankCapitalRatio=creditSupplier1.getCapitalRatio();
		double targetBankCapitalRatio=creditSupplier1.getTargetedCapitalAdequacyRatio();
		double aversionMarkUp=creditSupplier1.getRiskAversionMarkUp();
		// if the banks capital ratio is above the required level, decrease risk aversion, otherwise increase it
		if (bankCapitalRatio > targetBankCapitalRatio) {
			bankRiskAversion = bankRiskAversion - aversionMarkUp;
		}
		else {bankRiskAversion = bankRiskAversion + aversionMarkUp;}
		double probability=1/(1+Math.exp((operatingCashFlow-bankRiskAversion*demandedLoanPaymentsPerPeriod)/demandedLoanPaymentsPerPeriod));
		return probability;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.DefaultProbilityComputer#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1];//TODO
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.DefaultProbilityComputer#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {}
	

}
