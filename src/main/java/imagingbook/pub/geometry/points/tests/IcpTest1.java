package imagingbook.pub.geometry.points.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.Rotation;
import imagingbook.pub.geometry.mappings.linear.Translation;
import imagingbook.pub.geometry.points.IterativeClosestPointMatcher;

public class IcpTest1 {
	
	static int m = 50;
	static int size = 100;
	static double theta = 0.1;
	static double dx = 4;
	static double dy = -6;
	
	static double sigma = 2.5;

	
	static double tau = 0.1;
	static int kMax = 20;
	
	AffineMapping A = null;
	List<double[]> X, Y;
	
	Random rnd = new Random(11);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new IcpTest1().run();
	}
	
	void run() {
		A = makeTransformation();
		makeSamplePointsX();
		makeSamplePointsY();
		IterativeClosestPointMatcher icp = 
				new IterativeClosestPointMatcher(X, Y, tau, kMax);
		
		System.out.println("ICP has converged: " + icp.hasConverged());
	}

	private AffineMapping makeTransformation() {
		AffineMapping am = new AffineMapping();
		am.concatDestructive(new Translation(-50, -50));
		am.concatDestructive(new Rotation(theta));
		am.concatDestructive(new Translation(50, 50));
		am.concatDestructive(new Translation(dx, dy));
		return am;
	}

	private void  makeSamplePointsX() {
		X = new ArrayList<double[]>(m);
		for (int i = 0; i < m; i++) {
			double x = rnd.nextInt(size);
			double y = rnd.nextInt(size);
			X.add(new double[] {x, y});
		}
	}

	private void makeSamplePointsY() {
		Y = new ArrayList<double[]>(m);
		for (double[] xi : X) {
			double[] xiT = A.applyTo(xi);
			xiT[0] = xiT[0] + sigma * rnd.nextGaussian();
			xiT[1] = xiT[1] + sigma * rnd.nextGaussian();
			Y.add(xiT);
		}
		
	}
}
