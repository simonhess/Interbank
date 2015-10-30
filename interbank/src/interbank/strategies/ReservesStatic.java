package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort this class lets the central bank set a static reserves rate
 */
public class ReservesStatic extends AbstractStrategy implements
		ReservesRateStrategy {
	
	private double staticReservesRate;

	@Override
	public double computeReservesRate() {
		return staticReservesRate;
	}
	
	public double getStaticReservesRate() {
		return staticReservesRate;
	}

	public void setStaticReservesRate(double staticReservesRate) {
		this.staticReservesRate = staticReservesRate;
	}
	
	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		// TODO Auto-generated method stub

	}

	

}
