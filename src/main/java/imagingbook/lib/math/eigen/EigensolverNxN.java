package imagingbook.lib.math.eigen;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Matrix;

/**
 * Calculates eigenvalues and eigenvectors of square matrices of arbitrary size.
 * Eigenvalues are sorted by magnitude (in descending order). A matrix may have
 * complex eigenvalues but only real eigenvalues (and their associated
 * eigenvectors) are considered. 
 * Associated eigenvalues and eigenvectors share the same index (k = 0,...,N-1)
 * but are not sorted in any particular order.
 * Uses class {@link EigenDecomposition} of the
 * Apache Commons Math (3) library.
 * <br>
 * Usage example (retrieving all eigenvalues and associated eigenvectors):
 * <pre>
 * double[][] A = {
 * 	{ 5, 2, 0, 1 },
 * 	{ 2, 5, 0, 7 },
 * 	{ -3, 4, 6, 0 },
 * 	{ 1, 2, 3, 4 }};
 * 
 * RealEigensolver solver = new EigensolverNxN(A);
 * 
 * double[] eigenvals = solver.getEigenvalues();
 * for (int k = 0; k &lt; solver.getSize(); k++) {
 * 	double lambda = solver.getEigenvalue(k);
 * 	if (Double.isFinite(lambda)) {
 * 		double[] x = solver.getEigenvector(k);
 * 		...
 * 	}
 * }</pre>
 * 
 * @author WB
 * @version 2021/04/20
 */
public class EigensolverNxN implements RealEigensolver {

	private final int n;
	private final EigenDecomposition ed;
	private final double[] eVals;

	/**
	 * Constructor, takes a NxN (square) matrix.
	 * @param A a NxN matrix
	 */
	public EigensolverNxN(double[][] A) {
		if (!Matrix.isSquare(A)) {
			throw new IllegalArgumentException("matrix A must be square");
		}
		this.n = A.length;
		this.ed = new EigenDecomposition(MatrixUtils.createRealMatrix(A));
		this.eVals = makeEigenvalues(ed);
	}


	private double[] makeEigenvalues(EigenDecomposition ed) {
		double[] evals = new double[n];
		double[] re = ed.getRealEigenvalues();
		double[] im = ed.getImagEigenvalues();
		for (int i = 0; i < n; i++) {
			evals[i] = (Arithmetic.isZero(im[i])) ? re[i] : Double.NaN;
		}
		return evals;
	}

	@Override
	public int getSize() {
		return n;
	}
	
	@Override
	public boolean isReal() {
		return !ed.hasComplexEigenvalues();
	}

	@Override
	public double[] getEigenvalues() {
		return eVals;
	}

	@Override
	public double getEigenvalue(int k) {
		return eVals[k];
	}
	
	@Override
	public double[][] getEigenvectors() {
		return ed.getV().getData();
	}

	@Override
	public double[] getEigenvector(int k) {
		return ed.getEigenvector(k).toArray();
	}
	
	public EigenDecomposition getDecomposition() {
		return this.ed;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Not for any real use - this version is listed in the book.
	 * The real implementation is in {@link Matrix#eigen(RealMatrix)}.
	 * @param A a matrix
	 * @return the solution
	 */
	@SuppressWarnings("unused")
	private static EigenSolution eigen(RealMatrix A) {
		EigenDecomposition ed = new EigenDecomposition(A);
		RealVector eVals = MatrixUtils.createRealVector(ed.getRealEigenvalues());
		RealMatrix eVecs = ed.getV();
		return new EigenSolution(eVals, eVecs);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * @param args args
	 * @hidden
	 */
	public static void main(String[] args) {
//		double[][] A = {
//				{3, -2},
//				{-4, 1}};
//		double[][] A = {	// no real eigenvalues
//				{0, -1},
//				{2, 0}};
//		double[][] A = {	// no real eigenvalues
//				{4, -1},
//				{2, 4}};
//		double[][] A = {
//				{5, 2, 0},
//				{2, 5, 0},
//				{-3, 4, 6}};
		double[][] A = {	// 2 real eigenvalues
				{5, 2, 0, 1},
				{2, 5, 0, 7},
				{-3, 4, 6, 0},
				{1 , 2, 3, 4}};
		
		
		System.out.println("A = \n" + Matrix.toString(A));
		RealEigensolver solver = new EigensolverNxN(A);	

		System.out.println("isReal = " + solver.isReal());
		double[] eigenvals = solver.getEigenvalues();
		System.out.println("evals = " + Matrix.toString(eigenvals));
		System.out.println("evecs = \n" + Matrix.toString(solver.getEigenvectors()));
		for (int k = 0; k < solver.getSize(); k++) {
			double lambda = solver.getEigenvalue(k);
			if (Double.isFinite(lambda)) {
				double[] x = solver.getEigenvector(k);
				System.out.println();
				System.out.format("λ_%d = %.6f\n", k, lambda);
				System.out.format("x_%d = %s\n", k, Matrix.toString(x));
				System.out.format("   M*x = %s\n", Matrix.toString(Matrix.multiply(A, x)));
				System.out.format("   λ*M = %s\n", Matrix.toString(Matrix.multiply(lambda, x)));
			}
		}
		
		RealMatrix A2 = MatrixUtils.createRealMatrix(new double[][]
				{{2, 0, 1},
				 {0, 2, 0},
				 {1, 0, 2}});
		EigenSolution solution = eigen(A2);
		RealVector L = solution.getEigenValues();
		RealMatrix E = solution.getEigenVectors();
		for (int i = 0; i < L.getDimension(); i++) {
			System.out.println(L.getEntry(i));
			System.out.println(Matrix.toString(E.getColumn(i)));
		}
	}


}
