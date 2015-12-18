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

import interbank.StaticValues;
import interbank.strategies.MacroPrudentialStrategy;
import interbank.strategies.MonetaryPolicyStrategy;
import interbank.strategies.QEStrategy;
import interbank.strategies.ReservesRateStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;

import jmab.agents.AbstractBank;
import jmab.agents.AbstractFirm;
import jmab.agents.BondDemander;
import jmab.agents.BondSupplier;
import jmab.agents.CreditSupplier;
import jmab.agents.DepositDemander;
import jmab.agents.DepositSupplier;
import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.events.MacroTicEvent;
import jmab.goods.AbstractGood;
import jmab.goods.Bond;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import jmab.strategies.SupplyCreditStrategy;
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
	// Added new variables to support prudential and monetary policy
	protected double CAR;
	protected double liquidityRatio;
	protected double targetCreditToGDP;
	protected double monetaryMarkUp;
	// variables used to calculate GDP & inflation
	private int governmentPopulationId; // the id of the government
	private LinkedHashMap<Integer,Integer> goodPassedValueMap;
	private int[] gdpPopulationIds;//These are all the populations ids of agents that have either bought or produced goods entering in GDP
	private int[] gdpGoodsIds;//These are all the stock matrix ids of goods that enter in GDP
	private int[] gdpGoodsAges;//These are all age limit of goods that enter in GDP
	private int priceIndexProducerId;//This is the population id of agents that produce the goods entering in the CPI
	private int priceGoodId;//This is the stock matrix if of the good entering in the CPI
	private int realSaleId;//This is the id of the lagged value of real sales
	
	/**
	 * @return the advancesInterestRate
	 */
	public double getAdvancesInterestRate() {
		return advancesInterestRate;
	}

	/**
	 * @param advancesInterestRate the advancesInterestRate to set
	 */
	public void setAdvancesInterestRate(double advancesInterestRate) {
		this.advancesInterestRate = advancesInterestRate;
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
		else if (event.getTic()==StaticValues.TIC_CBPOLICY)
			// added new methods where the central bank determines its policies
			// first the cb calculates macro variables
			// by determining both the rates on advances & reserves (monetary)
			// as well as the supply of reserves and QE (moneteray)
			// , and finally several macroprudential policy tools
			this.calculateMacroVariables();
			this.determineReserveDepositInterestRate();
			this.determineAdvancesInterestRate();
			this.determineAdvancesSupply();
			//this.quantitativeEasing();
			this.determineMicroMacroprudentialPolicy();
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
				reserveRequirements.computePolicyTarget();
		// 2 cast and set the reserve requirements
				MacroPrudentialStrategy capitalBufferRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_CAPITALBUFFER);
				capitalBufferRatio.computePolicyTarget();
		// cast and set the net stable funding ratio and liquidity coverage ratio
		//MacroPrudentialStrategy netStableFundingRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_NETSTABLEFUNDING);
		//MacroPrudentialStrategy liquidityCoverageRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_LIQUIDITYCOVERAGE);
		//netStableFundingRatio.computePolicyTarget();
		//liquidityCoverageRatio.computePolicyTarget();
		// cast and set the capital buffer ratio and leverage ratio
		//MacroPrudentialStrategy capitalBufferRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_CAPITALBUFFER);
		//MacroPrudentialStrategy leverageRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_LEVERAGERATIO);
		//capitalBufferRatio.computePolicyTarget();
		//leverageRatio.computePolicyTarget();
		// cast and set debt to income ratio's 
		//MacroPrudentialStrategy incomeRatio = (MacroPrudentialStrategy)this.getStrategy(StaticValues.STRATEGY_INCOMERATIO);
		//incomeRatio.computePolicyTarget();
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
	
	/*
	 * This method is used to calculate / update the banks macroVariables and expectations
	 * 1. Expected natural rate
	 * 2. Expected potential output
	 */
	private void calculateMacroVariables(){
		SimulationController controller = (SimulationController)this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population pop = macroPop.getPopulation(priceIndexProducerId);
		// calculate and set inflation
		double totalSales=0;
		double averagePrice=0;
		for (Agent a:pop.getAgents()){
			AbstractFirm firm= (AbstractFirm) a;
			totalSales+=firm.getPassedValue(realSaleId, 0);
			AbstractGood good = (AbstractGood)firm.getItemStockMatrix(true, priceGoodId);
			averagePrice+=good.getPrice()*firm.getPassedValue(realSaleId,0);
		}
		double inflation = averagePrice/totalSales;
		setInflation(inflation);
		// calculate and set nominal GDP
		double gdpGoodsComponent=0;
		double pastInventories=0;
		double publicServantsWages=0;
		double nominalGDP=0;
		for(int popId:gdpPopulationIds){
			pop = macroPop.getPopulation(popId);
			//Population pop = macroPop.getPopulation(i); GET RID OF THIS?
			for(Agent j:pop.getAgents()){
				MacroAgent agent=(MacroAgent) j;
				for(int k=0; k<gdpGoodsIds.length;k++){
					List<Item> items= agent.getItemsStockMatrix(true, gdpGoodsIds[k]);
					for(Item item:items){
						if(item.getAge()<gdpGoodsAges[k]){
							gdpGoodsComponent+=item.getValue();
						}
						AbstractGood good = (AbstractGood)item;
						if(good.getProducer().getAgentId()==agent.getAgentId()){
							int passedValueId = goodPassedValueMap.get(good.getSMId());
							pastInventories+=agent.getPassedValue(passedValueId, 1);
						}
					}
				}					
			}
			gdpGoodsComponent-=pastInventories;
			if(governmentPopulationId!=-1){
				LaborDemander govt = (LaborDemander)macroPop.getPopulation(governmentPopulationId).getAgentList().get(0);
				for(MacroAgent agent:govt.getEmployees()){
					LaborSupplier publicServant = (LaborSupplier)agent;
					publicServantsWages+=publicServant.getWage();
				}
				nominalGDP = gdpGoodsComponent+publicServantsWages;
			}else
				nominalGDP = gdpGoodsComponent;
		}
		setNominalGDP(nominalGDP);
		// set expected natural rate depending on growth rate of output
		// 
		// TODO 
		//  add expected natural rate calculation here or in strategy? 
		// set expected potential output
		// TODO add expected potential output calculation here or do it in strategy?
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
	
}
