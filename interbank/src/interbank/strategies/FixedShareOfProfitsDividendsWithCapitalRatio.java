/**
 * 
 */
package interbank.strategies;

import java.nio.ByteBuffer;

import interbank.agents.Bank;
import interbank.agents.Households;
import jmab.agents.AbstractFirm;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Deposit;
import jmab.strategies.DividendsStrategy;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This is an extension of the general fixed shares of profits strategy which lets banks adjust dividend payments
 * as a result of the targeted capital ratio 
 */
@SuppressWarnings("serial")
public class FixedShareOfProfitsDividendsWithCapitalRatio extends AbstractStrategy implements DividendsStrategy {

	private int profitsLagId;
	private double profitShare;
	private int receiversId;
	private int depositId;
	private int reservesId;
	private double capitalRatioSensitivity;

	/* (non-Javadoc)
	 * @see jmab.strategies.DividendsStrategy#payDividends()
	 */
	@Override
	public void payDividends() {
		MacroAgent dividendPayer = (MacroAgent)this.agent;
		double profits = dividendPayer.getPassedValue(profitsLagId, 0);	
		if (profits>0){
			Population receivers = ((MacroPopulation)((SimulationController)this.scheduler).getPopulation()).getPopulation(receiversId);
			double totalNW = 0;
			for(Agent receiver:receivers.getAgents()){
				totalNW+=((MacroAgent)receiver).getNetWealth();
			}
			if (dividendPayer instanceof Bank){
				Deposit payerDep = (Deposit)dividendPayer.getItemStockMatrix(true, reservesId);
				//if(profits>payerDep.getValue()){
					//profits=payerDep.getValue();
				//}
				Bank bank= (Bank) dividendPayer;
				// added new part to increase or decrease dividend payments if the bank is above or below CR target
				// get the targeted capital ratio. 
				double targetedCapitalRatio = bank.getTargetedCapitalAdequacyRatio(); 
				// get the actual capital ratio
				double actualCapitalRatio = bank.getCapitalRatio();
				// get the sensitivity to capital ratios from the bank, 
				// determine a rule in which the bank increases dividends if it is above the capital ratio and decreases them if below
				if (targetedCapitalRatio < actualCapitalRatio) {
					bank.setDividends(profits*profitShare*(1+capitalRatioSensitivity));
				}
				else if (targetedCapitalRatio < actualCapitalRatio) {
					bank.setDividends(profits*profitShare*(1-capitalRatioSensitivity));
				}
				for(Agent rec:receivers.getAgents()){
					Households receiver =(Households) rec; 
					double nw = receiver.getNetWealth();
					Deposit recDep = (Deposit)receiver.getItemStockMatrix(true, depositId);
					double toPay;
					if(totalNW==0)
						toPay = profits*profitShare/receivers.getSize();
					else
						toPay= profits*profitShare*nw/totalNW;				
					recDep.setValue(recDep.getValue()+toPay);
					payerDep.setValue(payerDep.getValue()-toPay);
					Deposit otherBankReserves = (Deposit)((Bank)recDep.getLiabilityHolder()).getItemStockMatrix(true, reservesId);
					otherBankReserves.setValue(otherBankReserves.getValue()+toPay);
					receiver.setDividendsReceived(receiver.getDividendsReceived()+toPay);
				}
			}
			else{
				if (dividendPayer.getItemStockMatrix(true, depositId).getValue()>profits*profitShare){
					Deposit payerDep = (Deposit)dividendPayer.getItemStockMatrix(true, depositId);
					if(profits>payerDep.getValue()){
						profits=payerDep.getValue();
					}
					AbstractFirm firm= (AbstractFirm) dividendPayer;
					firm.setDividends(profits*profitShare);
					for(Agent rec:receivers.getAgents()){
						Households receiver =(Households) rec; 
						double nw = receiver.getNetWealth();
						double toPay;
						if(totalNW==0)
							toPay = profits*profitShare/receivers.getSize();
						else
							toPay= profits*profitShare*nw/totalNW;
						Deposit recDep = (Deposit)receiver.getItemStockMatrix(true, depositId);
						((Bank)payerDep.getLiabilityHolder()).transfer(payerDep, recDep,toPay);
						receiver.setDividendsReceived(receiver.getDividendsReceived()+toPay);
					}
				}
				
			}
		}


	}


	/**
	 * @return the reservesId
	 */
	public int getReservesId() {
		return reservesId;
	}


	/**
	 * @param reservesId the reservesId to set
	 */
	public void setReservesId(int reservesId) {
		this.reservesId = reservesId;
	}


	/**
	 * @return the profitsLagId
	 */
	public int getProfitsLagId() {
		return profitsLagId;
	}

	/**
	 * @param profitsLagId the profitsLagId to set
	 */
	public void setProfitsLagId(int profitsLagId) {
		this.profitsLagId = profitsLagId;
	}

	/**
	 * @return the profitShare
	 */
	public double getProfitShare() {
		return profitShare;
	}

	/**
	 * @param profitShare the profitShare to set
	 */
	public void setProfitShare(double profitShare) {
		this.profitShare = profitShare;
	}

	/**
	 * @return the receiversId
	 */
	public int getReceiversId() {
		return receiversId;
	}

	/**
	 * @param receiversId the receiversId to set
	 */
	public void setReceiversId(int receiversId) {
		this.receiversId = receiversId;
	}

	/**
	 * @return the depositId
	 */
	public int getDepositId() {
		return depositId;
	}

	/**
	 * @param depositId the depositId to set
	 */
	public void setDepositId(int depositId) {
		this.depositId = depositId;
	}

	public double getCapitalRatioSensitivity() {
		return capitalRatioSensitivity;
	}


	public void setCapitalRatioSensitivity(double capitalRatioSensitivity) {
		this.capitalRatioSensitivity = capitalRatioSensitivity;
	}


	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [profitShare][profitsLagId][receiversId][depositId][reservesId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(24);
		buf.putDouble(this.profitShare);
		buf.putInt(this.profitsLagId);
		buf.putInt(this.receiversId);
		buf.putInt(this.depositId);
		buf.putInt(this.reservesId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [profitShare][profitsLagId][receiversId][depositId][reservesId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.profitShare = buf.getDouble();
		this.profitsLagId = buf.getInt();
		this.receiversId = buf.getInt();
		this.depositId = buf.getInt();
		this.reservesId = buf.getInt();
	}
	
}
