/*
 * JMAB - Java Macroeconomic Agent Based Modeling Toolkit
 * Copyright (C) 2013 Alessandro Caiani and Antoine Godin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package interbank.agents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import interbank.StaticValues;
import interbank.strategies.MacroPrudentialStrategy;
import interbank.strategies.MonetaryPolicyStrategy;
import interbank.strategies.QEStrategy;
import interbank.strategies.ReservesRateStrategy;
import jmab.agents.AbstractBank;
import jmab.agents.BondDemander;
import jmab.agents.BondSupplier;
import jmab.agents.CreditSupplier;
import jmab.agents.DepositDemander;
import jmab.agents.DepositSupplier;
import jmab.agents.MacroAgent;
import jmab.events.MacroTicEvent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Bond;
import jmab.stockmatrix.Deposit;
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jabm.event.RoundFinishedEvent;

/**
 * @author Joeri Schasfoort & Alessandro Caiani and Antoine Godin
 * 
 *
 */

//TODO to be set active in the configuration file using the constructor, the CB is always active.
//TODO here not considered the possibility that CB buys bonds.
@SuppressWarnings("serial")
public class CentralBank extends AbstractBank implements CreditSupplier, DepositSupplier, BondDemander {

	private boolean QEActive = false;
	private double advancesInterestRate;
	private double reserveInterestRate;
	private int bondDemand;
	private double interestsOnAdvances;
	private double interestsOnBonds;
	private double bondInterestsReceived;
	protected double totInterestsReserves;
	// new monetary policy variables
	private double totalAdvancesSupply;
	//private BondSupplier selectedAssetSupplier;
	private int QEAssetDemand;
	protected double expectedNaturalRate;
	protected double expectedPotentialGDP;
	protected double nominalGDP;
	protected double inflation;
	// Added new variables to support prudential policy
	protected double CAR;
	protected double liquidityRatio;
	protected double targetCreditToGDP;
	protected double prudentialThreshold;
	protected double prudentialMarkUp;
	// Added new variables to support monetary policy
	protected double monetaryMarkUp;
	protected double monetaryPolicyMarkUp;
	protected double monetaryThreshold;
	protected double targetInflation;
	protected double maxReserveRequirement;
	protected double minReserveRequirement;
	protected double maxCAR;
	protected double minCAR;
	
	/**
	 * @return the advancesInterestRate
	 */
	public double getAdvancesInterestRate() {
		return advancesInterestRate;
	}

	public double getMonetaryPolicyMarkUp() {
		return monetaryPolicyMarkUp;
	}

	public void setMonetaryPolicyMarkUp(double monetaryPolicyMarkUp) {
		this.monetaryPolicyMarkUp = monetaryPolicyMarkUp;
	}

	public double getMonetaryThreshold() {
		return monetaryThreshold;
	}

	public void setMonetaryThreshold(double monetaryThreshold) {
		this.monetaryThreshold = monetaryThreshold;
	}

	public double getTargetInflation() {
		return targetInflation;
	}

	public void setTargetInflation(double targetInflation) {
		this.targetInflation = targetInflation;
	}

	/**
	 * @param advancesInterestRate the advancesInterestRate to set
	 */
	public void setAdvancesInterestRate(double advancesInterestRate) {
		this.advancesInterestRate = advancesInterestRate;
	}

	public double getPrudentialThreshold() {
		return prudentialThreshold;
	}

	public void setPrudentialThreshold(double prudentialThreshold) {
		this.prudentialThreshold = prudentialThreshold;
	}

	public double getPrudentialMarkUp() {
		return prudentialMarkUp;
	}

	public void setPrudentialMarkUp(double prudentialMarkUp) {
		this.prudentialMarkUp = prudentialMarkUp;
	}

	/**
	 * @return the reserveInterestRate
	 */
	public double getReserveInterestRate() {
		return reserveInterestRate;
	}

	public double getMonetaryMarkUp() {
		return monetaryMarkUp;
	}

	public void setMonetaryMarkUp(double monetaryMarkUp) {
		this.monetaryMarkUp = monetaryMarkUp;
	}

	/**
	 * @param reserveInterestRate the reserveInterestRate to set
	 */
	public void setReserveInterestRate(double reserveInterestRate) {
		this.reserveInterestRate = reserveInterestRate;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.MacroAgent#onRoundFinished(net.sourceforge.jabm.event.RoundFinishedEvent)
	 */
	@Override
	public void onRoundFinished(RoundFinishedEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jmab.agents.MacroAgent#initialiseCounterpart(net.sourceforge.jabm.agent.Agent, int)
	 */
	@Override
	public void initialiseCounterpart(Agent counterpart, int marketID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getInterestRate(jmab.agents.MacroAgent, double, int)
	 */
	@Override
	public double getInterestRate(int idLoanSM, MacroAgent creditDemander, double amount,
			int length) {
		return this.advancesInterestRate;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getLoanSupply(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double getLoanSupply(int loansId, MacroAgent creditDemander, double required) {
		return Double.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getDepositInterestRate(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double getDepositInterestRate(MacroAgent creditDemander,
			double amount) {
		return 0;
	}
	

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onTicArrived(jmab.events.AgentTicEvent)
	 */
	@Override
	protected void onTicArrived(MacroTicEvent event) {
		if(event.getTic()==StaticValues.TIC_COMPUTEEXPECTATIONS){ 
			this.interestsOnAdvances=0;
			this.interestsOnBonds=0;
			// compute expectations for monetary policy values
			computeExpectations();
			if (this.getItemsStockMatrix(true, StaticValues.SM_ADVANCES).size()!=0){
				List<Item> advances=this.getItemsStockMatrix(true,StaticValues.SM_ADVANCES);
				double advancesValue=0;
				for(int i=0;i<advances.size();i++){
					Loan advance=(Loan)advances.get(i);
					advancesValue+=advance.getValue();
				}
				this.interestsOnAdvances= advancesValue*this.advancesInterestRate;
			}
			if (this.getItemsStockMatrix(true, StaticValues.SM_BONDS).size()!=0){
				Bond bonds= (Bond) this.getItemStockMatrix(true, StaticValues.SM_BONDS);
				this.interestsOnBonds=bonds.getValue()*bonds.getInterestRate();
			}
			this.setActive(true, StaticValues.MKT_ADVANCES);
		}
		else if(event.getTic()==StaticValues.TIC_UPDATEEXPECTATIONS)
			this.cleanSM();
		else if (event.getTic()==StaticValues.TIC_CBBONDSPURCHASES)
			this.determineCBBondsPurchases();
		else if (event.getTic()==StaticValues.TIC_RESINTERESTS)
			this.payReservesInterests();
		else if (event.getTic()==StaticValues.TIC_CBPOLICY){
			// added new methods where the central bank determines its policies
			// by determining both the rates on advances & reserves (monetary)
			// as well as the supply of reserves and QE (moneteray)
			// , and finally several macroprudential policy tools
			this.determineReserveDepositInterestRate();
			this.determineAdvancesInterestRate();
			this.determineAdvancesSupply();
			//this.quantitativeEasing();
			this.determineMicroMacroprudentialPolicy();
		}
	}
	
	// Monetary and macroprudential policy methods
	
	/**
	 * This method lets the central bank set its macroprudential 
	 * policy tools according to some policy strategy
	 * 1. Capital requirements
	 * 2. Liquidity requirements / reserve requirements
	 */
	private void determineMicroMacroprudentialPolicy() {
		// 1 cast and set the capital ratio
				MacroPrudentialStrategy reserveRequirements = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_RESERVEREQUIREMENTS);
				this.liquidityRatio = reserveRequirements.computePolicyTarget();
		// 2 cast and set the reserve requirements
				MacroPrudentialStrategy capitalBufferRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_CAPITALBUFFER);
				this.CAR = capitalBufferRatio.computePolicyTarget();
		// 3 force banks to comply with the capital and liquidity ratio
				SimulationController controller = (SimulationController)this.getScheduler();
				MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
				Population banks = macroPop.getPopulation(StaticValues.BANKS_ID);
				for(Agent bTemp:banks.getAgents()){
					Bank b = (Bank) bTemp;
					// Check if their targeted CAR is above the required level, if not put it at that level
					double targetCAR = b.getTargetedCapitalAdequacyRatio();
					if (targetCAR < this.CAR)
						b.setTargetedCapitalAdequacyRatio(this.CAR);
					// check if their liquidity ratio is above the required level, if not put it at that level
					double targetLR = b.getTargetedLiquidityRatio();
					if (targetLR < this.liquidityRatio)
						b.setTargetedLiquidityRatio(this.liquidityRatio);
				}
	}
	/**
	 * This methods lets the central bank increase the amount of reserves in circulation
	 * by purchasing government bonds from banks
	 * The amount and from whom to purchase is based on the QuantitativeEasing strategy
	 */
	private void quantitativeEasing() {
		// determine total amount of reserve creation that can be spent to purchase QE assets
		QEStrategy strategy = (QEStrategy)this.getStrategy(StaticValues.STRATEGY_QUANTITATIVEEASING);
		// determine if the central bank is already in a QE program
		if (this.QEActive==false) {
			this.QEAssetDemand=strategy.assetDemand();
			// Purchase bonds from banks depending on strategy chosen
			if (this.QEAssetDemand>0){
				strategy.QEPurchase();
			}
		}
		else {
			// TODO maybe add some different input for QEPurchase method
			strategy.QEPurchase();
		}
	}
	/**
	 * This method lets the central bank determine how much advances it is willing to give
	 * to banks in need of liquidity. 
	 */
	private void determineAdvancesSupply() {
		/*
		SupplyCreditStrategy strategy=(SupplyCreditStrategy)this.getStrategy(StaticValues.STRATEGY_ADVANCESSUPPLY);
		// compute the supply of advances according to advances strategy
		double AdvancesSupply = strategy.computeCreditSupply();
		setTotalAdvancesSupply(AdvancesSupply);
		this.addValue(StaticValues.LAG_TOTADVANCESSUPPLY, AdvancesSupply);
		// activate the central bank in the market for advances if is has a positive supply
		if (this.getTotalAdvancesSupply()>0){
			this.setActive(true, StaticValues.MKT_ADVANCES);
			this.addToMarketPopulation(StaticValues.SM_ADVANCES, false);
		}
		*/	
		
	}
	/**
	 * This methods lets the central bank update the interest rate it pays to reserve holders
	 */
	private void determineReserveDepositInterestRate() {
		ReservesRateStrategy strategy = (ReservesRateStrategy)this.getStrategy(StaticValues.STRATEGY_RESDEPOSITRATE);
		this.reserveInterestRate=strategy.computeReservesRate();
		
	}
	/**
	 * This method lets the central bank update the interest it charges on advances using strategy advances
	 */
	private void determineAdvancesInterestRate() {
		MonetaryPolicyStrategy strategy = (MonetaryPolicyStrategy)this.getStrategy(StaticValues.STRATEGY_ADVANCES);
		this.advancesInterestRate=strategy.computeAdvancesRate();
		
	}
	/**
	 * This method allows the Central bank to pay interest to its reserve holders
	 * For every reserve account the 
	 */
	private void payReservesInterests() {
		List<Item> reserves = this.getItemsStockMatrix(false, StaticValues.SM_RESERVES);
		double totInterests=0;
		for(Item r:reserves){
			Deposit res = (Deposit)r;
			DepositDemander depositor = (DepositDemander)res.getAssetHolder();
			depositor.interestPaid(res.getInterestRate()*res.getValue());
			totInterests+= res.getInterestRate()*res.getValue();
			res.setValue(res.getValue()*(1+ res.getInterestRate()));	
		}
		totInterestsReserves=totInterests;

		
	}

	public double getCBProfits(){
		return this.interestsOnAdvances+this.interestsOnBonds;
	}
	
	private void determineCBBondsPurchases() {
		SimulationController controller = (SimulationController)this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(StaticValues.BANKS_ID);
		int banksBondDemand=0;
		// calculate the total banksBondDemand
		for(Agent b:banks.getAgents()){
			Bank tempB= (Bank) b;
			if(tempB.isActive(StaticValues.MKT_BONDS))
				banksBondDemand+=tempB.getBondDemand();
		}
		Population government=macroPop.getPopulation(StaticValues.GOVERNMENT_ID);
		
		Government gov= (Government) government.getAgentList().get(0);
		// calculate the total government bond supply
		int bondsSupply=gov.getBondSupply();
		// determine own demand
		this.bondDemand=bondsSupply-banksBondDemand;
		Bond bondsIssued = (Bond) gov.getItemStockMatrix(false, StaticValues.SM_BONDS, gov); 
		if (bondsIssued!=null && bondDemand>0){
			//1. Determine quantity, price and total costs
			double price=bondsIssued.getPrice();
			double interestRate=bondsIssued.getInterestRate();
			int maturity=bondsIssued.getMaturity();
			Bond bondsPurchased = new Bond(price*bondDemand, (double)bondDemand, this, gov, maturity, interestRate, price);
			bondsIssued.setQuantity(bondsIssued.getQuantity()-bondDemand);
			// transaction increase bonds and decrease bonds government
			this.addItemStockMatrix(bondsPurchased, true, StaticValues.SM_BONDS);
			gov.addItemStockMatrix(bondsPurchased, false, StaticValues.SM_BONDS);
			// increase government reserves
			Item govRes= (Item) gov.getItemStockMatrix(true, StaticValues.SM_RESERVES);
			govRes.setValue(govRes.getValue()+price*bondDemand);
			//7. If there are no more bonds to be sold, then the supplier is deactivated.
			if (bondsIssued.getQuantity()==0){
				gov.removeItemStockMatrix(bondsIssued, false, StaticValues.SM_BONDS);
				gov.setActive(false, StaticValues.MKT_BONDS);
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getTotalLoansSupply()
	 */
	@Override
	public double getTotalLoansSupply(int loansId) {
		return Double.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#setTotalLoansSupply(double)
	 */
	@Override
	public void setTotalLoansSupply(int loansId, double d) {}

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onAgentArrival(net.sourceforge.jabm.event.AgentArrivalEvent)
	 */
	@Override
	public void onAgentArrival(AgentArrivalEvent event) {
		
	}

	/**
	 * This is overriden because the central bank is having special cases.
	 * a. In the case of a transfer of reserves between two banks
	 * b. In the case of doing Government transfers
	 */
	@Override
	public void transfer(Item paying, Item receiving, double amount){
		AbstractBank otherBank =  (AbstractBank)receiving.getLiabilityHolder();
		//If the central bank is both the payer and the receiver
		if(otherBank.getAgentId()==this.getAgentId()){
			paying.setValue(paying.getValue()-amount);
			receiving.setValue(receiving.getValue()+amount);
		//If the central bank is doing Government transfers, then it needs to update the reserve account of the government,
		// the deposit account of the receiver and the reserve account of the bank holding the deposit
		}else if(paying.getAssetHolder().getPopulationId()==StaticValues.GOVERNMENT_ID){
			paying.setValue(paying.getValue()-amount);
			receiving.setValue(receiving.getValue()+amount);
			Item oBankRes = otherBank.getCounterpartItem(receiving, paying);
			oBankRes.setValue(oBankRes.getValue()+amount);
		}else{
			super.transfer(paying, receiving, amount);
		}
	}
	
	
	
	@Override
	public Item getCounterpartItem(Item liability, Item otherLiability){
		return otherLiability;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#getBondsDemand(double, jmab.agents.BondSupplier)
	 */
	@Override
	public int getBondsDemand(double price, BondSupplier issuer) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#getPayingStocks(int, jmab.goods.Item)
	 */
	@Override
	public List<Item> getPayingStocks(int idBondSM, Item payableStock) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#setBondInterestsReceived(double)
	 */
	@Override
	public void setBondInterestsReceived(double interests) {
		this.bondInterestsReceived = interests;
		
	}

	/**
	 * @return the bondInterestsReceived
	 */
	public double getBondInterestsReceived() {
		return bondInterestsReceived;
	}
	
	
	public double getTotalAdvancesSupply() {
		return totalAdvancesSupply;
	}

	public void setTotalAdvancesSupply(double totalAdvancesSupply) {
		this.totalAdvancesSupply = totalAdvancesSupply;
	}

	public int getQEAssetDemand() {
		return QEAssetDemand;
	}

	public void setQEAssetDemand(int qEAssetDemand) {
		this.QEAssetDemand = qEAssetDemand;
	}

	public double getExpectedNaturalRate() {
		return expectedNaturalRate;
	}

	public void setExpectedNaturalRate(double expectedNaturalRate) {
		this.expectedNaturalRate = expectedNaturalRate;
	}

	public double getExpectedPotentialGDP() {
		return expectedPotentialGDP;
	}

	public void setExpectedPotentialGDP(double expectedPotentialGDP) {
		this.expectedPotentialGDP = expectedPotentialGDP;
	}

	public double getNominalGDP() {
		return nominalGDP;
	}

	public void setNominalGDP(double nominalGDP) {
		this.nominalGDP = nominalGDP;
	}

	public double getInflation() {
		return inflation;
	}

	public void setInflation(double inflation) {
		this.inflation = inflation;
	}
	
	public double getCAR() {
		return CAR;
	}

	public void setCAR(double cAR) {
		CAR = cAR;
	}

	public double getLiquidityRatio() {
		return liquidityRatio;
	}

	public void setLiquidityRatio(double liquidityRatio) {
		this.liquidityRatio = liquidityRatio;
	}

	public double getTargetCreditToGDP() {
		return targetCreditToGDP;
	}

	public void setTargetCreditToGDP(double targetCreditToGDP) {
		this.targetCreditToGDP = targetCreditToGDP;
	}

	/**
	 * Populates the agent characteristics using the byte array content. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][advancesInterestRate][reserveInterestRate][interestsOnAdvances][interestsOnBonds]
	 * [bondInterestsReceived][bondDemand][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public void populateAgent(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] macroBytes = new byte[buf.getInt()];
		buf.get(macroBytes);
		super.populateCharacteristics(macroBytes, pop);
		advancesInterestRate = buf.getDouble();
		reserveInterestRate = buf.getDouble();
		interestsOnAdvances = buf.getDouble();
		interestsOnBonds = buf.getDouble();
		bondInterestsReceived = buf.getDouble();
		bondDemand = buf.getInt();
		int matSize = buf.getInt();
		if(matSize>0){
			byte[] smBytes = new byte[matSize];
			buf.get(smBytes);
			this.populateStockMatrixBytes(smBytes, pop);
		}
		int expSize = buf.getInt();
		if(expSize>0){
			byte[] expBytes = new byte[expSize];
			buf.get(expBytes);
			this.populateExpectationsBytes(expBytes);
		}
		int lagSize = buf.getInt();
		if(lagSize>0){
			byte[] lagBytes = new byte[lagSize];
			buf.get(lagBytes);
			this.populatePassedValuesBytes(lagBytes);
		}
		int stratSize = buf.getInt();
		if(stratSize>0){
			byte[] stratBytes = new byte[stratSize];
			buf.get(stratBytes);
			this.populateStrategies(stratBytes, pop);
		}
	}

	/**
	 * Generates the byte array containing all relevant informations regarding the central bank agent. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][advancesInterestRate][reserveInterestRate][interestsOnAdvances][interestsOnBonds]
	 * [bondInterestsReceived][bondDemand][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] charBytes = super.getAgentCharacteristicsBytes();
			out.write(ByteBuffer.allocate(4).putInt(charBytes.length).array());
			out.write(charBytes);
			ByteBuffer buf = ByteBuffer.allocate(44);
			buf.putDouble(advancesInterestRate);
			buf.putDouble(reserveInterestRate);
			buf.putDouble(interestsOnAdvances);
			buf.putDouble(interestsOnBonds);
			buf.putDouble(bondInterestsReceived);
			buf.putInt(bondDemand);
			out.write(buf.array());
			byte[] smBytes = super.getStockMatrixBytes();
			out.write(ByteBuffer.allocate(4).putInt(smBytes.length).array());
			out.write(smBytes);
			byte[] expBytes = super.getExpectationsBytes();
			out.write(ByteBuffer.allocate(4).putInt(expBytes.length).array());
			out.write(expBytes);
			byte[] passedValBytes = super.getPassedValuesBytes();
			out.write(ByteBuffer.allocate(4).putInt(passedValBytes.length).array());
			out.write(passedValBytes);
			byte[] stratsBytes = super.getStrategiesBytes();
			out.write(ByteBuffer.allocate(4).putInt(stratsBytes.length).array());
			out.write(stratsBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the stockMatrix with the byte array content. The structure of the stock matrix is the following:
	 * [nbStockTypes]
	 * for each type of stocks
	 * 	[IdStock][nbItems]
	 * 		for each Item
	 * 			[itemSize][itemStructure]
	 * 		end for
	 * end for 	
	 */
	@Override
	public void populateStockMatrixBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbStockTypes = buf.getInt();
		for(int i = 0 ; i < nbStockTypes ; i++){
			int stockId = buf.getInt();
			int nbStocks = buf.getInt();
			for(int j = 0 ; j < nbStocks ; j++){
				int itemSize = buf.getInt();
				byte[] itemData = new byte[itemSize];
				buf.get(itemData);
				Item it;
				switch(stockId){
				case StaticValues.SM_ADVANCES:
					it = new Loan(itemData, pop, this);
					break;
				default:
					it = new Bond(itemData, pop, this);
					break;
				}
				this.addItemStockMatrix(it, true, stockId);
				MacroAgent liabHolder = it.getLiabilityHolder();
				liabHolder.addItemStockMatrix(it, false, stockId);
			}
		}	
	}

	public double getMaxReserveRequirement() {
		return maxReserveRequirement;
	}

	public void setMaxReserveRequirement(double maxReserveRequirement) {
		this.maxReserveRequirement = maxReserveRequirement;
	}

	public double getMinReserveRequirement() {
		return minReserveRequirement;
	}

	public void setMinReserveRequirement(double minReserveRequirement) {
		this.minReserveRequirement = minReserveRequirement;
	}

	public double getMaxCAR() {
		return maxCAR;
	}

	public void setMaxCAR(double maxCAR) {
		this.maxCAR = maxCAR;
	}

	public double getMinCAR() {
		return minCAR;
	}

	public void setMinCAR(double minCAR) {
		this.minCAR = minCAR;
	}


	
}
