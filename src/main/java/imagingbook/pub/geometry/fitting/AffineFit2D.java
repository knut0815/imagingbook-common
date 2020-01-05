package imagingbook.pub.geometry.fitting;

import static imagingbook.lib.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Point;

/**
 * TODO: see ProjectiveMapping2D.fromNPoints(Point[] P, Point[] Q) -- remove 1 version (either fit or mapping)!
 *
 */
public class AffineFit2D implements LinearFit2D {
	
	private final RealMatrix A;		// the calculated transformation matrix
	private final double err;		// the calculated error
	
	/**
	 * Constructor.
	 * Fits two sequences of 2D points using an affine transformation model.
	 * At least 3 point pairs are required. For 3 point pairs, the solution
	 * is an exact fit, otherwise a least-squares fit is found.
	 * @param P the source points
	 * @param Q the target points
	 */
	public AffineFit2D(Point[] P, Point[] Q) {	// 
		checkSize(P, Q);
		int m = P.length;
		
		RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 6);
		RealVector b = new ArrayRealVector(2 * m);
		
		// mount the matrix M
		int row = 0;
		for (Point p : P) {
			M.setEntry(row, 0, p.getX());
			M.setEntry(row, 1, p.getY());
			M.setEntry(row, 2, 1);
			row++;
			M.setEntry(row, 3, p.getX());
			M.setEntry(row, 4, p.getY());
			M.setEntry(row, 5, 1);
			row++;
		}
		
		// mount vector b
		row = 0;
		for (Point q : Q) {
			b.setEntry(row, q.getX());
			row++;
			b.setEntry(row, q.getY());
			row++;
		}
		
		// solve M * a = b (for the unknown parameter vector a):
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector a = solver.solve(b);
		A = makeTransformationMatrix(a);
		err = getError(P, Q, A);
	}

	// creates a n x (n+1) transformation matrix from the elements of a
	private RealMatrix makeTransformationMatrix(RealVector a) {
		RealMatrix A = MatrixUtils.createRealMatrix(2, 3);
		int i = 0;
		for (int row = 0; row < 2; row++) {
			// get (n+1) elements from a and place in row
			for (int j = 0; j < 3; j++) {
				A.setEntry(row, j, a.getEntry(i));
				i++;
			}
		}
		return A;
	}
	
	// --------------------------------------------------------
	
	protected static void checkSize(Point[] P, Point[] Q) {
		if (P.length < 3 || Q.length < 3) {
			throw new IllegalArgumentException("At least 3 point pairs are required to calculate this fit");
		}
	}
	
	/**
	 * Calculates and returns the cumulative distance error
	 * between the two point sequences under the transformation A,
	 * i.e., {@code e = sum_i (||p_i * A - q_i||)}.
	 * @param P	the first point sequence
	 * @param Q the second point sequence
	 * @param A	a (2 x 3) affine transformation matrix
	 * @return the error {@code e}
	 */
	protected static double getError(Point[] P, Point[] Q, RealMatrix A) {
		int m = Math.min(P.length,  Q.length);
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			Point p = P[i];
			Point q = Q[i];
			Point p2 = Point.create(A.operate(Matrix.toHomogeneous(p.toArray())));
			errSum = errSum + 
					Math.sqrt(sqr(q.getX() - p2.getX()) + sqr(q.getY() - p2.getY()));
		}
		return errSum;
	}
	
	// --------------------------------------------------------

	@Override
	public RealMatrix getTransformationMatrix() { // TODO: should return a mapping!?
		return A;
	}

	@Override
	public double getError() {
		return err;
	}

}
