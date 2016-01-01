public class GridWorld {

	private Game game;
	private double discountFactor = 0.99;
    private double nemultiplier = 1.5;

    private boolean isEstimate1;
    private boolean isEstimate2;
    private boolean isEstimate3;
    private boolean isEstimate4;
    private boolean isEstimate5;

    public GridWorld(Game game)
	{
		this.game = game;
	}

	public static void main(String[] args) {

        /* first, set up the grid */
		Game game = new Game();

        System.out.println("Part 1.1");
		GridWorld gameGridWorld = new GridWorld(game);
		//System.out.println("Value Iteration:");
		double[][] valIterUtils = gameGridWorld.ValueIteration();
		//game.printUtilities();

        System.out.println("\nPart 1.2");
		double[][] QLearnUtils = gameGridWorld.ReinforcementLearning();
		//System.out.println("TD Q-Learning Utilities:");
		//game.printUtilities();
		//System.out.println("TD Q-Learning Q Values:");
		//game.printQLearning();

        int iter = 0;
        while (iter < 400) {
            double[][] rValIterUtils = gameGridWorld.ValueIteration();
            double[][] rQLearnUtils = gameGridWorld.ReinforcementLearning();

            System.out.println(calcRMSE(rValIterUtils, rQLearnUtils, game.States.length, game.States[0].length));
            iter++;
        }

	}
	
	private static double calcRMSE(double[][] estimateUtils, double[][] actualUtils , int xLength, int yLength)
	{
		double err = 0.0;
		
		for(int i = 0; i < xLength; i++)
		{
			for(int j = 0; j < yLength; j++)
			{
				double temp = (estimateUtils[i][j] - actualUtils[i][j])*(estimateUtils[i][j] - actualUtils[i][j]);
				err += temp;
			}
		}
		
		return err/(xLength * yLength);
	}
	
	private void SetNewUtilityWithBellman(State currState)
	{
		
		double maxExpectedUtility = Integer.MIN_VALUE;

		
		for(Movement movement : Movement.values())
		{
			double sumU = 0.0;
			for(State nextState: this.game.GetNeighborStates(currState))
			{
				if(!this.game.IsOutBound(nextState) && !nextState.IsWall){
					sumU += this.game.GetProbability(currState, movement, nextState) * nextState.GetUtility();
				}else
				{
					sumU += this.game.GetProbability(currState, movement, nextState) * currState.GetUtility();
				}
			}
			
			
			if(sumU > maxExpectedUtility)
			{
				maxExpectedUtility = sumU;

				currState.OptimalPolicy = movement;
			}
		}
		
		double newUtil = currState.GetReward() + discountFactor * maxExpectedUtility;

//        if(isEstimate1) {
//            //System.out.println("utility estimate 1: ");
//            System.out.println(newUtil);
//        }

//        if (isEstimate2) {
//            //System.out.println("utility estimate 2: ");
//            System.out.println(newUtil);
//        }
//        if (isEstimate3) {
//            //System.out.println("utility estimate 3: ");
//            System.out.println(newUtil);
//        }
//        if (isEstimate4) {
//            //System.out.println("utility estimate 4: ");
//            System.out.println(newUtil);
//        }
//        if (isEstimate5) {
//            //System.out.println("utility estimate 5: ");
//            System.out.println(newUtil);
//        }

		currState.SetUtility(newUtil);
	}
	
	public double[][] ValueIteration()
	{
		int numIteration = 25;
        //int numIteration = 1100;
		int iter = 0;
		double[][] allUtils = new double[this.game.States.length][this.game.States[0].length];

        /* for utility estimate */
        isEstimate1 = false;
        isEstimate2 = false;
        isEstimate3 = false;
        isEstimate4 = false;
        isEstimate5 = false;

        /* Apply Bellman Equation to utility */
		while(iter < numIteration) {
			for(int i = 0; i < this.game.States.length; i++) {
				for(int j = 0; j < this.game.States[0].length; j++) {
					State s = this.game.States[i][j];
					if(!s.IsWall && !s.IsTerminal) {

//                        if(i == 1 && j == 2) {
//                            isEstimate1 = true;
//                            isEstimate2 = false;
//                            isEstimate3 = false;
//                            isEstimate4 = false;
//                            isEstimate5 = false;
//                        } else if (i == 3 && j == 1) {
//                            isEstimate1 = false;
//                            isEstimate2 = true;
//                            isEstimate3 = false;
//                            isEstimate4 = false;
//                            isEstimate5 = false;
//                        } else if (i == 5 && j == 1) {
//                            isEstimate1 = false;
//                            isEstimate2 = false;
//                            isEstimate3 = true;
//                            isEstimate4 = false;
//                            isEstimate5 = false;
//                        } else if (i == 5 && j == 4) {
//                            isEstimate1 = false;
//                            isEstimate2 = false;
//                            isEstimate3 = false;
//                            isEstimate4 = true;
//                            isEstimate5 = false;
//                        } else if (i == 4 && j == 5) {
//                            isEstimate1 = false;
//                            isEstimate2 = false;
//                            isEstimate3 = false;
//                            isEstimate4 = false;
//                            isEstimate5 = true;
//                        } else {
//                            isEstimate1 = false;
//                            isEstimate2 = false;
//                            isEstimate3 = false;
//                            isEstimate4 = false;
//                            isEstimate5 = false;
//                        }
						this.SetNewUtilityWithBellman(this.game.States[i][j]);	
					}
				}	
			}
			iter++;
		}
		
		for(int i = 0; i < this.game.States.length; i++)
		{
			for(int j = 0; j < this.game.States[0].length; j++)
			{
				allUtils[i][j] = this.game.States[i][j].GetUtility();
			}
		}
		
		return allUtils;
	}

	
	public double[][] ReinforcementLearning()
	{


		State currState = this.game.StartState;

		double[][] allUtils = new double[this.game.States.length][this.game.States[0].length];
		
		while(true)
		{	
			currState.TimeStep = currState.TimeStep + 1;
			Movement selectedMovement = this.selectAction(currState);
			currState.CountActionTaken(selectedMovement);
			State nextState = this.getSuccessorState(currState, selectedMovement);
			this.TDUpdate(currState, nextState, selectedMovement);
		
			currState = nextState;
			if(currState.IsTerminal) {

                break;
			}
		}
		
		for(int i = 0; i < this.game.States.length; i++)
		{
			for(int j = 0; j < this.game.States[0].length; j++)
			{
				allUtils[i][j] = this.game.States[i][j].GetUtility();
			}
		}
		
		return allUtils;
	}
	
	private double explorationFuction(double expectedUtil, int numTimesTaken)
	{
        /* Less step, more likely to navigate to the new area
         * Our utility is around 2.2~2.8, so if the chance to "explore" will likely to happen
         * if the step is less than 20
         * around same in 20~30,
         * less otherwise - this time we assume that every grid is explored.
         */

		int N_e = 50;
		if (numTimesTaken < N_e) {
			return (double)N_e * nemultiplier/(double)numTimesTaken;
		}else
		{
			return expectedUtil;
		}
	}
	
	private double getAlpha(int timeStep)
	{
		return 60.0/(59.0 + timeStep);
	}
	
	private void TDUpdate(State currState, State succState, Movement selectedMovement)
	{
		double alpha = this.getAlpha(currState.TimeStep);
		double currQ = currState.GetQValue(selectedMovement);
		double maxQForSucc;
		double newCurrQ = 0.0;
		maxQForSucc = succState.GetUtility();

        /* equation from the lecture */
		newCurrQ = currQ + alpha*(currState.GetReward() + discountFactor * maxQForSucc - currQ );

		currState.UpdateQValues(selectedMovement, newCurrQ, isEstimate1, isEstimate2, isEstimate3, isEstimate4, isEstimate5);
	}
	
	private State getSuccessorState(State currState, Movement selectedMovement)
	{
		double rand = Math.random();
		int nextXCoord = 0;
		int nextYCoord = 0;
		
		if(rand < 0.1) {
			if(selectedMovement == Movement.UP) {
				nextXCoord = currState.XCoord - 1;
				nextYCoord = currState.YCoord;
			}else if(selectedMovement == Movement.DOWN) {
				nextXCoord = currState.XCoord + 1;
				nextYCoord = currState.YCoord;
			}else if(selectedMovement == Movement.LEFT) {
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord - 1;
			}else {
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord + 1;
			}
		}else if(rand < 0.2)
        {
			if(selectedMovement == Movement.UP) {
				nextXCoord = currState.XCoord + 1;
				nextYCoord = currState.YCoord;
			}else if(selectedMovement == Movement.DOWN) {
				nextXCoord = currState.XCoord - 1;
				nextYCoord = currState.YCoord;
			}else if(selectedMovement == Movement.LEFT) {
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord + 1;
			}else {
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord - 1;
			}
		}else {
			if(selectedMovement == Movement.UP)
			{
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord + 1;
			}else if(selectedMovement == Movement.DOWN)
			{
				nextXCoord = currState.XCoord;
				nextYCoord = currState.YCoord - 1;
			}else if(selectedMovement == Movement.LEFT)
			{
				nextXCoord = currState.XCoord - 1;
				nextYCoord = currState.YCoord;
			}else
			{
				nextXCoord = currState.XCoord + 1;
				nextYCoord = currState.YCoord;
			}
		}
		if (!(nextXCoord >= 0 && nextXCoord < this.game.States.length 
				&& nextYCoord >=0 && nextYCoord < this.game.States[0].length))
        {
			return currState;
		}


		if(!this.game.States[nextXCoord][nextYCoord].IsWall)
		{


			return this.game.States[nextXCoord][nextYCoord];
		}else /* when it goes to wall */
		{
			return currState;
		}
	}
	
	private Movement selectAction(State currState) {
		double max = Integer.MIN_VALUE;
		Movement ret = Movement.UP;

		for(Movement movement : Movement.values())
		{
			double v = this.explorationFuction(currState.GetQValue(movement), currState.GetActionTakenCount(movement));
			if(v > max)
			{
				ret = movement;
				max = v;
			}
		}
		return ret;
	}
	
	
}
