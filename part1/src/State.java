import java.util.Hashtable;


public class State {

	private double utility;
	private double reward;
	public boolean IsWall;
	public int XCoord;
	public int YCoord;
	public int TimeStep;

	private int numUpTaken;
	private int numDownTaken;
	private int numLeftTaken;
	private int numRightTaken;
	private Hashtable<Movement, Double> QValues;
	
	public Movement OptimalPolicy;
	public boolean IsTerminal;
	
	private static final double DEFAULT_REWARD = -0.04;

	public State(int x, int y) {
		this.TimeStep = 1;
		this.XCoord = x;
		this.YCoord = y;
		this.IsWall = false;
		this.reward = DEFAULT_REWARD;
		this.utility = 0.0;
		
		this.numDownTaken = 0;
		this.numUpTaken = 0;
		this.numLeftTaken = 0;
		this.numRightTaken = 0;
		
		this.QValues = new Hashtable<Movement, Double>();
		QValues.put(Movement.UP, 0.0);
		QValues.put(Movement.DOWN, 0.0);
		QValues.put(Movement.LEFT, 0.0);
		QValues.put(Movement.RIGHT, 0.0);
	}
	
	
	public double GetQValue(Movement movement)
	{
		return this.QValues.get(movement);
	}
	
	public void UpdateQValues(Movement movement, double newVal, boolean isEstimate1, boolean isEstimate2,
                              boolean isEstimate3, boolean isEstimate4, boolean isEstimate5) {
		this.QValues.put(movement, newVal);
		//UPDATE Policy too
		double max = Integer.MIN_VALUE;
		for(Movement a: Movement.values())
		{

			if(this.QValues.get(a) > max)
			{
				max = this.QValues.get(a);
				this.OptimalPolicy = a;
				this.utility = max;

                if(isEstimate1) {
                    //System.out.println("timestep : " + this.TimeStep);
                    //System.out.println(max);

                }

                if (isEstimate2) {
                    //System.out.println("utility estimate 2: ");
                    //System.out.println(newUtil);
                }
                if (isEstimate3) {
                    //System.out.println("utility estimate 3: ");
                    //System.out.println(newUtil);
                }
                if (isEstimate4) {
                    //System.out.println("utility estimate 4: ");
                    //System.out.println(newUtil);
                }
                if (isEstimate5) {
                    //System.out.println("utility estimate 5: ");
                    //System.out.println(newUtil);
                }
			}
		}
	}
	public void CountActionTaken(Movement movement) {
		if(movement == Movement.UP)
		{
			this.numUpTaken++;
		}
		else if(movement == Movement.DOWN)
		{
			this.numDownTaken++;
		}
		else if(movement == Movement.LEFT)
		{
			this.numLeftTaken++;
		}
		else if(movement == Movement.RIGHT)
		{
			this.numRightTaken++;
		}
	}
	
	public int GetActionTakenCount(Movement movement)
	{
		if(movement == Movement.UP)
		{
			return this.numUpTaken;
		}
		else if(movement == Movement.DOWN)
		{
			return this.numDownTaken;
		}
		else if(movement == Movement.LEFT)
		{
			return this.numLeftTaken;
		}
		else
		{
			return this.numRightTaken;
		}
	}
		
	public double GetUtility() {
		return this.utility;
	}

	public void SetUtility(double utility) {
		this.utility = utility;
	}
	
	public double GetReward() {
		return this.reward;
	}
	
	public void SetReward(double reward) {
		this.reward = reward;
	}
	
	
}
