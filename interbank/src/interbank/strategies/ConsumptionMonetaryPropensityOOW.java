/**
 * 
 */
package interbank.strategies;

import java.nio.ByteBuffer;

import interbank.agents.Households;
import jmab.agents.AbstractHousehold;
import jmab.population.MacroPopulation;
import jmab.strategies.ConsumptionStrategy;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author joerischasfoort
 *
 */
public class ConsumptionMonetaryPropensityOOW extends AbstractStrategy implements ConsumptionStrategy {

	double propensityOOW;
	double propensityOOI;
	int pastConsumptionId;
	int consPriceExpectationID; //in the config file will be specified as equal to the corresponding static field. 

	
	/**
	 * @return the propensityOOW
	 */
	public double getPropensityOOW() {
		return propensityOOW;
	}


	/**
	 * @param propensityOOW the propensityOOW to set
	 */
	public void setPropensityOOW(double propensityOOW) {
		this.propensityOOW = propensityOOW;
	}


	/**
	 * @return the propensityOOI
	 */
	public double getPropensityOOI() {
		return propensityOOI;
	}


	/**
	 * @param propensityOOI the propensityOOI to set
	 */
	public void setPropensityOOI(double propensityOOI) {
		this.propensityOOI = propensityOOI;
	}


	/**
	 * @return the consPriceExpectationID
	 */
	public int getConsPriceExpectationID() {
		return consPriceExpectationID;
	}


	/**
	 * @param consPriceExpectationID the consPriceExpectationID to set
	 */
	public void setConsPriceExpectationID(int consPriceExpectationID) {
		this.consPriceExpectationID = consPriceExpectationID;
	}

	/**
	 * @return the pastConsumptionId
	 */
	public int getPastConsumptionId() {
		return pastConsumptionId;
	}


	/**
	 * @param pastConsumptionId the pastConsumptionId to set
	 */
	public void setPastConsumptionId(int pastConsumptionId) {
		this.pastConsumptionId = pastConsumptionId;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.ConsumptionStrategy#computeRealConsumptionDemand()
	 */
	@Override
	public double computeRealConsumptionDemand() {
		Households household= (Households) this.getAgent(); 
		double priceExpectation=household.getExpectation(consPriceExpectationID).getExpectation();
		double netIncome=household.getNetIncome();
		double netWealth=household.getNetWealth();
		double oldDepositRate=household.getPreviousDepositRate();
		double newDepositRate = household.getCurrentDepositRate();
		double markUp = (newDepositRate*100 - oldDepositRate*100) / (oldDepositRate*100);
		double demand=(propensityOOI*(netIncome/priceExpectation)+(propensityOOW + markUp)*(netWealth/priceExpectation));
		return demand;
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [propensityOOW][propensityOOI][persistency][consPriceExpectationID][pastConsumptionId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(32);
		buf.putDouble(this.propensityOOW);
		buf.putDouble(this.propensityOOI);
		buf.putInt(this.consPriceExpectationID);
		buf.putInt(this.pastConsumptionId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [propensityOOW][propensityOOI][persistency][consPriceExpectationID][pastConsumptionId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.propensityOOW = buf.getDouble();
		this.propensityOOI = buf.getDouble();
		this.consPriceExpectationID = buf.getInt();
		this.pastConsumptionId = buf.getInt();
	}

}
