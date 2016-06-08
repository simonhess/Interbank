package interbank.strategies;

import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort this class lets the central bank set a static reserves rate
 */
public class ReservesStatic extends AbstractStrategy implements
		ReservesRateStrategy {
	
	private double staticReservesRate;
	private int shockRound;
	private double shockAdd;

	@Override
	public double computeReservesRate() {
		SimulationController controller = (SimulationController)this.scheduler;
		int round = ((MacroSimulation)controller.getSimulation()).getRound();
		if(round >shockRound)
			return staticReservesRate+shockAdd;
		return staticReservesRate;
	}
	
	public double getStaticReservesRate() {
		return staticReservesRate;
	}

	public void setStaticReservesRate(double staticReservesRate) {
		this.staticReservesRate = staticReservesRate;
	}
	
	public int getShockRound() {
		return shockRound;
	}

	public void setShockRound(int shockRound) {
		this.shockRound = shockRound;
	}

	public double getShockAdd() {
		return shockAdd;
	}

	public void setShockAdd(double shockAdd) {
		this.shockAdd = shockAdd;
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
