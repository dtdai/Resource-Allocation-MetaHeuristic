package algorithm;

import definition.VirtualMachine;
import definition.PhysicalMachine;
import java.util.ArrayList;

/**
 *
 * @author TrongDai
 */
public class GameModel {

    private final ArrayList<PhysicalMachine> pms;
    private final ArrayList<VirtualMachine> vms;
    private final int k; // Amount of Resource
    private final int u; // Amount of virtual machine
    private final int[][] R;
    private final double[][] H;
    private final double[][] g;
    private final double d;
    private final int alpha;
    private double Omega = 0.0;
    private double Phil = 0.0;
    private double FairUtil = 0.0;

    public GameModel(ArrayList<PhysicalMachine> pms, ArrayList<VirtualMachine> vms, int k, int alpha) {
        this.pms = pms;
        this.vms = vms;
        this.u = vms.size();
        this.k = k;
        this.alpha = alpha;

        R = RequirementMatrix(this.vms);
        H = NormalizedMatrix(R);
        g = NormalizedDemands(H);
        d = DominantShare(g);
        Omega = FairAllocation(d, g);
    }

    public double getOmega() {
        return Omega;
    }

    private int[][] RequirementMatrix(ArrayList<VirtualMachine> vms) {
        int[][] RM = new int[u][k];
        for (int i = 0; i < vms.size(); i++) {
            RM[i][0] = vms.get(i).getCore();
            RM[i][1] = vms.get(i).getRam();
            RM[i][2] = vms.get(i).getDisk();
        }
        return RM;
    }

    private int[] SumResourceCalCulating() {
        int[] sumResPM = new int[k];
        for (int i = 0; i < pms.size(); i++) {
            sumResPM[0] += pms.get(i).getCore();
            sumResPM[1] += pms.get(i).getRam();
            sumResPM[2] += pms.get(i).getDisk();
        }
        return sumResPM;
    }

    private double[][] NormalizedMatrix(int[][] RM) {
        double[][] HM = new double[u][k];
        int[] sumResPM = SumResourceCalCulating();
        for (int i = 0; i < u; i++) {
            for (int j = 0; j < k; j++) {
                HM[i][j] = (1.0 * RM[i][j]) / (1.0 * sumResPM[j]);
            }
        }
        return HM;
    }

    private double[][] NormalizedDemands(double[][] HM) {
        double[][] GM = new double[u][k];
        double[] HMax = new double[u];
        for (int i = 0; i < u; i++) {
            HMax[i] = HM[i][0];
            for (int j = 1; j < k; j++) {
                if (HMax[i] < HM[i][j]) {
                    HMax[i] = HM[i][j];
                }
            }
        }
        for (int i = 0; i < u; i++) {
            for (int j = 0; j < k; j++) {
                GM[i][j] = HM[i][j] / HMax[i];
            }
        }
        return GM;
    }

    private double DominantShare(double[][] GM) {
        double Dmax = 0.0;
        for (int j = 0; j < k; j++) {
            double sumG = 0.0;
            for (int i = 0; i < u; i++) {
                sumG += GM[i][j];
            }
            if (Dmax < sumG) {
                Dmax = sumG;
            }
        }
        return Math.pow(Dmax, -1);
    }

    private double FairAllocation(double D, double[][] G) {
        double omega = 0.0;
        for (int i = 0; i < u; i++) {
            for (int j = 0; j < k; j++) {
                omega += Math.pow(Math.abs(H[i][j] - D * G[i][j]), alpha - 1);
            }
        }
        return Math.pow(omega, 1.0 / alpha);
    }

    public double ResourceUtilization(ArrayList<PhysicalMachine> pms) {
        double res = 0.0;
        for (int i = 0; i < pms.size(); i++) {
            double sum = 0.0;
            double util = pms.get(i).Utilization() != 0 ? pms.get(i).Utilization() : 1;
            sum = sum + Math.pow(pms.get(i).CoreUtilize() / util - 1, 2);
            sum = sum + Math.pow(pms.get(i).RamUtilize() / util - 1, 2);
            sum = sum + Math.pow(pms.get(i).DiskUtilize() / util - 1, 2);
            res += Math.sqrt(sum);
        }
        return res;
    }

    // F(A) = sgn(1 - alpha) * omega - phil
    public double FairnessUtilization(ArrayList<PhysicalMachine> pms) {
        this.Phil = ResourceUtilization(pms);
//        this.FairUtil = Math.signum(1 - this.alpha) * this.Omega - this.Phil; // -> Negative value
//        this.FairUtil = 1 / Math.abs(this.FairUtil);
//        this.FairUtil = 1 / (1 + Math.exp(this.FairUtil));
        this.FairUtil = 1 / (this.Omega + this.Phil);
        return this.FairUtil;
    }
}
