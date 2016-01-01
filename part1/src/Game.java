import java.util.ArrayList;


public class Game {

	public State[][] States;
	public State StartState;


    public Game() {
        /* create a new grid world */
        this.States = new State[6][6];

        for(int i = 0; i < this.States.length; i++)
        {
            for(int j = 0; j < this.States[0].length; j++)
            {
                this.States[i][j] = new State(i, j);
            }
        }

        this.StartState = this.States[1][2];
        this.States[0][0].SetReward(1);
        this.States[1][0].SetReward(-1);
        this.States[1][5].SetReward(-1);
        this.States[4][0].SetReward(-1);
        this.States[4][4].SetReward(-1);
        this.States[5][0].SetReward(-1);
        this.States[5][3].SetReward(3);

        this.States[0][0].SetUtility(1);
        this.States[1][0].SetUtility(-1);
        this.States[1][5].SetUtility(-1);
        this.States[4][0].SetUtility(-1);
        this.States[4][4].SetUtility(-1);
        this.States[5][0].SetUtility(-1);
        this.States[5][3].SetUtility(3);

        /* comment this area when getting non - terminal states */
        this.States[0][0].IsTerminal = true;
        this.States[1][0].IsTerminal = true;
        this.States[1][5].IsTerminal = true;
        this.States[4][0].IsTerminal = true;
        this.States[4][4].IsTerminal = true;
        this.States[5][0].IsTerminal = true;
        this.States[5][3].IsTerminal = true;

        this.States[3][0].IsWall = true;
        this.States[3][2].IsWall = true;
        this.States[3][3].IsWall = true;
        this.States[3][4].IsWall = true;

    }

	public void printQLearning()
	{
		for(int i = 0; i < this.States.length; i++)
		{
			for(int j = 0; j < this.States[0].length; j++)
			{
				if(this.States[i][j].IsWall)
				{
					System.out.print("( WALL : 0.0 )");
				}else if(this.States[i][j].IsTerminal)
				{
					System.out.print("( TERMINAL )") ;
				}else{
					System.out.print("( *" + this.States[i][j].OptimalPolicy + "*)" );
					System.out.print("("+ Movement.UP + ":" + this.States[i][j].GetQValue(Movement.UP) + "|"
							+ Movement.DOWN + ":" + this.States[i][j].GetQValue(Movement.DOWN) + "|"
							+ Movement.LEFT + ":" + this.States[i][j].GetQValue(Movement.LEFT) + "|"
							+ Movement.RIGHT + ":" + this.States[i][j].GetQValue(Movement.RIGHT) + ")");
				}
			}
			System.out.println();
		}
	}
	
	public void printUtilities()
	{
		
		for(int i = 0; i < this.States.length; i++)
		{
			for(int j = 0; j < this.States[0].length; j++)
			{
				if(this.States[i][j].IsWall)
				{
					System.out.println("( WALL )");
				}else if(this.States[i][j].GetReward() == 1 || this.States[i][j].GetReward() == -1)
				{
					//System.out.println("( TERMINAL " + this.States[i][j].GetUtility() + ")");
                    System.out.println("( TERMINAL " + this.States[i][j].GetUtility() + "," + this.States[i][j].OptimalPolicy + ") ");
				}else{
				System.out.print("(" + this.States[i][j].GetUtility() + "," + this.States[i][j].OptimalPolicy + ") ");
				}
			}
			System.out.println();
		}
	}


	public boolean IsOutBound(State state)
	{
		return (state.XCoord < 0 || state.XCoord >= this.States.length || state.YCoord < 0 || state.YCoord >= this.States[0].length);
	}
	
	public State GetNextState(State currState, Movement movementTaken)
	{
		int nextX;
		int nextY;
		
		if(movementTaken == Movement.UP) {
			nextX = currState.XCoord;
			nextY = currState.YCoord + 1;
		}else if(movementTaken == Movement.DOWN) {
			nextX = currState.XCoord;
			nextY = currState.YCoord - 1;
		}else if(movementTaken == Movement.LEFT) {
			nextX = currState.XCoord - 1;
			nextY = currState.YCoord;
		}else {
			nextX = currState.XCoord + 1;
			nextY = currState.YCoord;
		}
		
		if(nextX < 0 || nextX >= this.States.length || nextY < 0 || nextY >= this.States[0].length)
		{
			return new State(nextX, nextY); 
		}else{
			return this.States[nextX][nextY];
		}
	}
	
	public ArrayList<State> GetNeighborStates(State currState)
	{
		ArrayList<State> states = new ArrayList<State>();

		for(Movement movement : Movement.values()){
			states.add(this.GetNextState(currState, movement));
		}
		return states;
	}
	

	public double GetProbability(State currState, Movement movement, State nextState)
	{
		if(movement == Movement.LEFT)
		{
			if(currState.XCoord - 1 == nextState.XCoord && currState.YCoord == nextState.YCoord) {
				return 0.8; // 1 - rand
			}else if(currState.XCoord == nextState.XCoord 
					&& (currState.YCoord - 1 == nextState.YCoord
                    || currState.YCoord + 1 == nextState.YCoord)) {
				return 0.1;
			}else {
				return 0.0;
			}
		}else if(movement == Movement.RIGHT)
		{
			if(currState.XCoord + 1 == nextState.XCoord && currState.YCoord == nextState.YCoord) {
				return 0.8;
			}else if(currState.XCoord == nextState.XCoord 
					&& (currState.YCoord - 1 == nextState.YCoord
                    || currState.YCoord + 1 == nextState.YCoord)) {
				return 0.1;
			}else {
				return 0.0;
			}
		}else if(movement == Movement.UP)
		{
			if(currState.XCoord == nextState.XCoord && currState.YCoord + 1== nextState.YCoord)
			{
				return 0.8;
			}else if(currState.YCoord == nextState.YCoord 
					&& (currState.XCoord - 1 == nextState.XCoord
                    || currState.XCoord + 1 == nextState.XCoord)) {
				return 0.1;
			}else {
				return 0.0;
			}
		}else if(movement == Movement.DOWN)
		{
			if(currState.XCoord == nextState.XCoord && currState.YCoord - 1== nextState.YCoord) {
				return 0.8;
			}else if(currState.YCoord == nextState.YCoord 
					&& (currState.XCoord - 1 == nextState.XCoord
                    || currState.XCoord + 1 == nextState.XCoord)) {
				return 0.1;
			}else {
				return 0.0;
			}
		}else { // not possible
			return -1.0;
		}
		
	}

}
