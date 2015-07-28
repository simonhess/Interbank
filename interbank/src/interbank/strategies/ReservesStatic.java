package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

public class ReservesStatic extends AbstractStrategy implements
		ReservesRateStrategy {
	
	private double staticReservesRate;

	
	public double getStaticReservesRate() {
		return staticReservesRate;
	}

	public void setStaticReservesRate(double staticReservesRate) {
		this.staticReservesRate = staticReservesRate;
	}

	@Override
	public double computeReservesRate() {
		// compute?? 
		return 0;
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
