package android.prompt.n2mf.global;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 
 * @author prompt
 * @CreatedOn: 30th May, 2016
 * @Purpose: To solve the Linear Programming Problems (LPP using the Simplex
 *           method, One/Two Phase). Here we are taking the double data type to
 *           solve the problem (note: as per requirement float data type can
 *           also used)
 * 
 *           Simplex Mathematical formula to solve the Linear Programming
 *           Problems, for maximize the profit and minimize the cost. If the
 *           minimize problem that time simplex method will check the artificial
 *           variable comes in 1-phase or not. If the maximize problem that time
 *           simplex method will work as a one phase. Both constructor will use
 *           as per user specification/demand. Here Zj-Cj is taken as to decide
 *           when to stop the iteration (i.e. min(Zj-Cj) >=0 then the iteration
 *           will stop. If Artificial variable is present in function and
 *           constraint that time it will check the var[] as Artificial variable
 *           has removed or not, if not then iteration stop immediately other
 *           wise it will goes for 2nd phase iteration
 *
 */
public class SimplexLP {
	// simplex table as in double two dimension array
	private double[][] a;
	// zCoefficient is main function coefficient
	// e.g. Minimize Z= 2X1+5X2+21X3+15X4
	// sCoefficient is bases on the constraint slack or surplus variable with
	// function of Minimize Z coefficient
	// e.g. Minimize Z= 2X1+5X2+21X3+15X4 +0S1+0S2+0S3+0S4+0S5 (i.e. for 5
	// constraint, as per no of constraint count the S coefficient take part in
	// the function)
	// cjCoefficient is final Maximize/Minimize function which will take part in
	// iteration
	// pervCjCoefficient is final Maximize/Minimize function which will take
	// part in firstPhase iteration
	// aCoefficient is artifical values as similar to sCoefficient
	private double[] zCoefficient, cjCoefficient, sCoefficient,
			pervCjCoefficient;// , aCoefficient;;
	// N is total no of count column which actually take part in iteration
	// (i.e. 0 to N-1 is all constraint coefficient, Nth column is the
	// base/condition right side value, and N+1th column is the coefficient of
	// function which will taken part for base
	// M is total no of count row which actually taken part in iteration
	// (i.e. 0 to M-1 is all constraint, Mth row is the Zj-Cj value
	// corresponding to the each column)
	private int N, M;
	// isTwoPhaseProblem is helps to decide the problem is require two phase
	// technique or not
	// isDual require to convert Minimize function to Maximize, so the function
	// will multiple with -1 and the solution also multiply with -1
	// isMaximize helps to determine the problem, true if problem is maximize
	// otherwise false
	@SuppressWarnings("unused")
	private boolean isTwoPhaseProblem, isDual, isMaximize;
	// var is a value of function which will use to solve function as
	// Minimize/Maximize
	public int[] var;
	// Z is the solution for Maximize/Minimize
	public double Z;
	// isFeasibleSolution after checking the Zj-Cj, and the var as no artificial
	// value present that time it will be true other wise false
	public boolean isFeasibleSolution;
	// bCoefficientSolved is a final value correspondent to the
	// minimize/maximize the function, it will taken respectively from the var[]
	public double[] bCoefficientSolved;
	// secondPhase comes when problem has require isTwoPhase (i.e.
	// isTwoPhaseProblem=true)
	public SimplexLP secondPhase;
	// some time the a[i,j] value is zero in this case N/0 gives infinite, so
	// when this happen more than 2 time (consecutive), that time need to break
	// the loop, becuase in both the lopping for pivot the row and column will
	// same
	private int countPivotRowColZero;
	// when twophase problem and there is a artifical variable present in the
	// base Cj array, that time there will be more than one solution found
	public boolean isThereMultipleSolution;

	/**
	 * 
	 * @param isTwoPhase
	 *            true if problem require two phase method/iteration otherwise
	 *            false
	 * @param isDualRequire
	 *            true if problem require duality otherwise false (i.e. for
	 *            Minimize function will convert to the Maximize function)
	 * @param isMaximizeProblem
	 *            true if problem is for Maximize otherwise false (i.e. there is
	 *            only two problem either Maximize or Minimize)
	 * @param constraintArray
	 *            two dimension double array for (matrix M*N, M=Row, N=Column)
	 *            left side constraint, require constraint include the
	 *            surplus/slack/artificial/constraint coefficient
	 * @param baseArray
	 *            one dimension double array for right side constraint
	 * @param zcoefficient
	 *            one dimension double array for actual function' main variable
	 *            coefficient (i.e. with out converting the dual)
	 * @param cjSArray
	 *            one dimension double array for actual function' surplus/slack
	 *            variable coefficient (i.e. with out converting the dual)
	 * @param cjArtificalArray
	 *            one dimension double array for actual function' artificial
	 *            variable coefficient (i.e. with out converting the dual, for
	 *            minimize the function other wise empty array)
	 * @param baseCjArray
	 *            one dimension double array for starting the iteration as
	 *            column no for the problem with respect to the
	 *            Minimize/Maximize (i.e. if artificial present in the
	 *            coefficient then must take column no of the artificial other
	 *            wise column no of surplus/slack. Mostly it will comes from the
	 *            identity matrix column no.)
	 */
	public SimplexLP(boolean isTwoPhase, boolean isDualRequire,
			boolean isMaximizeProblem, double[][] constraintArray,
			double[] baseArray, double[] zcoefficient, double[] cjSArray,
			double[] cjArtificalArray, int[] baseCjArray) {

		if (isTwoPhase && !isDualRequire) {
			isTwoPhase = false;
		}
		if (!isMaximizeProblem && !isTwoPhase) {
			isTwoPhase = true;
		}
		if (isTwoPhase) {
			if (cjArtificalArray == null
					|| (cjArtificalArray != null && cjArtificalArray.length == 0)) {
				cjArtificalArray = new double[baseArray.length];
			}
		}
		if (isDualRequire || (isTwoPhase && !isMaximizeProblem)) {
			for (int index = 0; index < zcoefficient.length; index++)
				zcoefficient[index] = (isDualRequire ? -1 : 1)
						* zcoefficient[index];
			if (cjArtificalArray != null)
				for (int index = 0; index < cjArtificalArray.length; index++)
					cjArtificalArray[index] = (isDualRequire ? -1 : 1)
							* cjArtificalArray[index];
		}
		this.isDual = isDualRequire;
		this.isMaximize = isMaximizeProblem;
		this.isTwoPhaseProblem = isTwoPhase;

		init(constraintArray, baseArray, zcoefficient, cjSArray,
				cjArtificalArray, baseCjArray, null);
	}

	/**
	 * if the LP require for Minimize that time make Z* from Z
	 * 
	 * @param isTwoPhase
	 *            true if problem require two phase method/iteration otherwise
	 *            false
	 * @param isDualRequire
	 *            true if problem require duality otherwise false (i.e. for
	 *            Minimize function will convert to the Maximize function, here
	 *            in this only Z will convert/multiply with -1)
	 * @param constraintArray
	 *            two dimension double array for (matrix M*N, M=Row, N=Column)
	 *            left side constraint, require constraint include the
	 *            surplus/slack/artificial/constraint coefficient
	 * @param baseArray
	 *            one dimension double array for right side constraint
	 * @param zcoefficient
	 *            one dimension double array for actual function' main variable
	 *            coefficient (i.e. with converting the dual/minimize)
	 * @param cjSArray
	 *            one dimension double array for actual function' surplus/slack
	 *            variable coefficient (i.e. with converting the dual)
	 * @param cjArtificalArray
	 *            one dimension double array for actual function' artificial
	 *            variable coefficient (i.e. with converting the dual/minimize,
	 *            for minimize the function other wise empty array)
	 * @param baseCjArray
	 *            one dimension double array for starting the iteration as
	 *            column no for the problem with respect to the
	 *            Minimize/Maximize (i.e. if artificial present in the
	 *            coefficient then must take column no of the artificial other
	 *            wise column no of surplus/slack. Mostly it will comes from the
	 *            identity matrix column no.)
	 */
	public SimplexLP(boolean isTwoPhase, boolean isDualRequire,
			double[][] constraintArray, double[] baseArray,
			double[] zcoefficient, double[] cjSArray,
			double[] cjArtificalArray, int[] baseCjArray,
			double[] pervcjCoefficient) {
		this.isDual = isDualRequire;
		this.isTwoPhaseProblem = isTwoPhase;
		init(constraintArray, baseArray, zcoefficient, cjSArray,
				cjArtificalArray, baseCjArray, pervcjCoefficient);
	}

	/**
	 * if the LP require for Minimize that time make Z* from Z, default
	 * isTwoPhaseProblem = true, isDual = true
	 * 
	 * @param constraintArray
	 *            two dimension double array for (matrix M*N, M=Row, N=Column)
	 *            left side constraint, require constraint include the
	 *            surplus/slack/artificial/constraint coefficient
	 * @param baseArray
	 *            one dimension double array for right side constraint
	 * @param zcoefficient
	 *            one dimension double array for actual function' main variable
	 *            coefficient (i.e. with converting the dual/minimize)
	 * @param cjSArray
	 *            one dimension double array for actual function' surplus/slack
	 *            variable coefficient (i.e. with converting the dual)
	 * @param cjArtificalArray
	 *            one dimension double array for actual function' artificial
	 *            variable coefficient (i.e. with converting the dual/minimize,
	 *            for minimize the function other wise empty array)
	 * @param baseCjArray
	 *            one dimension double array for starting the iteration as
	 *            column no for the problem with respect to the
	 *            Minimize/Maximize (i.e. if artificial present in the
	 *            coefficient then must take column no of the artificial other
	 *            wise column no of surplus/slack. Mostly it will comes from the
	 *            identity matrix column no.)
	 */
	public SimplexLP(double[][] constraintArray, double[] baseArray,
			double[] zcoefficient, double[] cjSArray,
			double[] cjArtificalArray, int[] baseCjArray) {
		this.isTwoPhaseProblem = this.isDual = true;
		init(constraintArray, baseArray, zcoefficient, cjSArray,
				cjArtificalArray, baseCjArray, null);
	}

	/**
	 * initialize the method for iteration
	 * 
	 */
	private void init(double[][] constraintArray, double[] baseArray,
			double[] zcoefficient, double[] cjSArray,
			double[] cjArtificalArray, int[] baseCjArray,
			double[] pervcjCoefficient) {

		this.countPivotRowColZero = 0;
		this.isThereMultipleSolution = false;

		this.zCoefficient = zcoefficient;
		this.sCoefficient = cjSArray;
		if (pervcjCoefficient != null) {
			this.pervCjCoefficient = pervcjCoefficient;
		}
		this.cjCoefficient = new double[zcoefficient.length
				+ (cjSArray != null ? cjSArray.length : 0)
				+ (cjArtificalArray != null ? cjArtificalArray.length : 0)];

		int index = 0;
		for (; index < zcoefficient.length; index++)
			this.cjCoefficient[index] = this.isTwoPhaseProblem ? 0
					: zcoefficient[index];
		if (cjSArray != null)
			for (; index < zcoefficient.length + cjSArray.length; index++)
				this.cjCoefficient[index] = cjSArray[index
						- zcoefficient.length];
		if (cjSArray != null && cjArtificalArray != null)
			for (; index < zcoefficient.length + cjSArray.length
					+ cjArtificalArray.length; index++)
				this.cjCoefficient[index] = cjArtificalArray[index
						- (zcoefficient.length + cjSArray.length)];

		this.initialize(constraintArray, baseArray, this.cjCoefficient,
				baseCjArray);
	}

	/**
	 * This method will start the iteration and store the solution in Z, var,
	 * bCoefficientSolved, if isFeasibleSolution is true
	 */
	public void startIteration() {
		isFeasibleSolution = solve(-1) != -1;
		if (this.isFeasibleSolution && this.isTwoPhaseProblem) {
			// there is a feasible solution and if it is two phase

			double[] newBase = new double[M];// a.length - 1];
			for (int i = 0; i < newBase.length; i++) {
				newBase[i] = round(a[i][N]);
			}
			double[][] newAA = new double[newBase.length][this.zCoefficient.length
					+ this.sCoefficient.length];
			for (int i = 0; i < newBase.length; i++) {
				for (int j = 0; j < (this.zCoefficient.length + this.sCoefficient.length); j++) {
					newAA[i][j] = round(a[i][j]);
				}
			}

			secondPhase = new SimplexLP(false, isDual, newAA, newBase,
					zCoefficient, sCoefficient, new double[] {}, var,
					cjCoefficient);

			secondPhase.isFeasibleSolution = secondPhase.solve(-1) != -1;
			// secondPhase.Z =
			// secondPhase.a[a.length-1][secondPhase.a[secondPhase.a.length-1].length-1]*(isDual?
			// -1:1);
			if (secondPhase.isFeasibleSolution) {
				// show feasible solution from second phase
				secondPhase.bCoefficientSolved = new double[secondPhase.M];
				for (int index = 0; index < secondPhase.bCoefficientSolved.length; index++)
					secondPhase.bCoefficientSolved[index] = secondPhase.a[index][secondPhase.N];
				secondPhase.Z *= (isDual ? -1 : 1);
				// System.out.println(secondPhase.Z);
			} else {
				// show feasible solution not found
				// System.out.println("No Feasible solution found");
			}
		} else if (isFeasibleSolution) {
			// show feasible solution
			bCoefficientSolved = new double[M];
			for (int index = 0; index < bCoefficientSolved.length; index++)
				bCoefficientSolved[index] = a[index][N];
			Z *= (isDual ? -1 : 1);
			// System.out.println(Z);
		} else {
			// show feasible solution not found
			// System.out.println("No Feasible solution found");

		}
	}

	/**
	 * This method will initialize the all requirement to the start the
	 * iteration
	 * 
	 * @param A
	 *            constraint coefficient including the surplus/slack/artificial
	 *            i.e. left side constraint
	 * @param b
	 *            coefficient of base i.e. right side constraint
	 * @param c
	 *            coefficient of final function i.e. Z's coefficient
	 * @param cb
	 *            the identity matrix's 1's column value/no
	 */
	private void initialize(double[][] A, double[] b, double[] c, int[] cb) {
		M = b.length;
		var = cb;
		N = c.length;
		// M = no of constraint, +1 = to count zj
		// N = no of column of constraint, +1 = b, and +1 = co-efficient of j
		// column
		a = new double[M + 1][N + 2];// [M+N+1];

		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				a[i][j] = A[i][j];

		// putting Cj
		for (int j = 0; j < N; j++) {
			a[M][j] = c[j];
		}
		// putting base value
		for (int i = 0; i < M; i++) {
			// a[i][M+N] = b[i];
			a[i][N] = b[i];
		}
		// putting base cj value
		for (int i = 0; i < M; i++) {

			// some time artificial variable present in 2nd phase, in that time
			// the pervious CjCoefficient require, otherwise index out of bound
			// error found
			a[i][N + 1] = ((cb[i] >= cjCoefficient.length && pervCjCoefficient != null) ? pervCjCoefficient[cb[i]]
					: cjCoefficient[cb[i]]);
			// a[i][N + 1] = cjCoefficient[cb[i]];
		}

	}

	/**
	 * Start the iteration, to solve the function
	 * 
	 * @param initalValue
	 *            is a default value to check/validate the isFeasibleSolution
	 *            (i.e. -1)
	 * @return column no if return -1 means no feasible solution other wise
	 *         solution is there
	 */
	private int solve(int initalValue) {
		int row = 0, column = initalValue;// pivot column or end
		ArrayList<Integer> columns;
		double minRowValue, minColumnValue, minColumnValueCheck, valuebaseColumn;
		while (true) {
			for (int j = 0; j <= N; j++) {// < if base not go for Z, <= if Z
											// want to calculate
				// calculating zj
				double sum = 0;
				for (int i = 0; i < M; i++) {
					sum += (a[i][j] * a[i][N + 1]);
				}
				a[M][j] = sum;
				// calculating zj-cj
				if (j != N)
					a[M][j] -= cjCoefficient[j];
				a[M][j] = round(a[M][j]);
			}
			Z = round(a[M][N]);
			// for(int j=0;j<N;j++ )
			// a[M][j] -= cj[j];

			// require to go for next step, by finding
			minRowValue = a[M][0];
			minColumnValue = 999999999.99999999999;
			for (int j = 0; j < N; j++) {
				if (minRowValue >= a[M][j]) {
					minRowValue = a[M][j];
					column = j;
				}
			}
			// some case values @Mth row like column 0,0,0,-2,-2,-2,0,0,-2,0,...
			// So in PHPSImplex.com site taking 1st mathced value, because here
			// in above case it takes 1st matched value, so we need to do
			// same:Start
			// if (minRowValue <= 0)
			columns = new ArrayList<Integer>();
			for (int j = 0; j < N; j++) {
				if (minRowValue == a[M][j]) {
					columns.add(j);
					column = j;
					break;
				}
			}
			// End

			if (minRowValue >= 0 || countPivotRowColZero > 1)// ||
																// (isTwoPhaseProblem
																// && (Z == 0 ||
																// Z == -0)))
			{
				if (isTwoPhaseProblem) {
					if (Z != 0) {
						// when two phase that time we need to check with the
						// Z's value, in Two phase problem, 1st iteration always
						// have Z=0, when all Zj-Cj/Cj-Zj become >=/<= 0.
						column = -1;
						break;
					}

					for (int i = 0; i < M; i++) {
						if (var[i] >= N - M) {
							// there are multiple solution present
							isThereMultipleSolution = true;
							break;
						}
					}
				}
				// end the iteration
				break;
			} else {
				  // dividing the base with qth column value //checking the min ratio
				int actualRow = 0;
				for (int index = 0; index < columns.size(); index++)
				{
					minColumnValueCheck = 999999999.99999999999;
					for (int i = 0; i < M; i++)
					{
						if (a[i][columns.get(index)] > 0)
						{
							valuebaseColumn = a[i][N] / a[i][columns.get(index)];
							if (valuebaseColumn >= 0
									&& minColumnValueCheck >= valuebaseColumn)
							{
								minColumnValueCheck = a[i][N] / a[i][columns.get(index)];
								row = i;
							}
						}
					}

					if (minColumnValue >= minColumnValueCheck) //|| ( index !=0 && row<zCoefficient.length))if ((minColumnValue < minColumnValueCheck || index == 0) && minColumnValueCheck != 999999999.99999999999)
					{

						minColumnValue = minColumnValueCheck;
						column = columns.get(index);
						actualRow = row;
					}
				}
				row = actualRow;
				for (int i = 0; i < M; i++) {
					if (a[i][column] > 0) {
						valuebaseColumn = a[i][N] / a[i][column];
						if (valuebaseColumn >= 0
								&& minColumnValue >= valuebaseColumn) {
							minColumnValue = a[i][N] / a[i][column];
							row = i;
						}
					}
				}
			}
			// calculate the pivot row
			pivot(row, column, minRowValue, minColumnValue);
		}
		return column;
	}

	/**
	 * To make/find the pivot row from the pivot column
	 * 
	 * @param row
	 *            pivot row value
	 * @param column
	 *            pivot column value
	 * @param minRowValue
	 *            the minimum value because of this pivot row found
	 * @param minColumnValue
	 *            the minimum value because of this pivot column found
	 */
	public void pivot(int row, int column, double minRowValue,
			double minColumnValue) {
		if (a[row][column] == 0) 
		{
			++countPivotRowColZero;
			return;
		}
		countPivotRowColZero = 0;
		
		for (int i = 0; i < M; i++)
			for (int j = 0; j <= N; j++)
				if (i != row && j != column)
					a[i][j] -= a[row][j] * a[i][column] / a[row][column];

		// zero out column q
		for (int i = 0; i < M; i++)
			if (i != row)
				a[i][column] = 0.0;

		// scale row p
		for (int j = 0; j <= N; j++)
			if (j != column)
				a[row][j] /= a[row][column];

		a[row][column] = 1.0;
		var[row] = column;
		// set co-efficient of Cj bases on pivot column
		a[row][N + 1] = cjCoefficient[column];
	}

	/**
	 * To get decimal points from the numeric value, in this algorithm we are taking care up to 3 decimal points (i.e. rounding 3 decimal)
	 *
	 * @param value
	 *            the actual numeric value
	 * @return numeric value having the specified precision value
	 */
	public static double round(double value) {
		return new BigDecimal(value).setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
