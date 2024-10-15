package algorithm;

import definition.Machine;
import definition.PhysicalMachine;
import definition.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author TrongDai
 */
public class HybridACOSA {

    private final int numAnts;
    private final int numPM;
    private final int numVM;
    private final ArrayList<PhysicalMachine> pms;
    private final ArrayList<VirtualMachine> vms;
    private final ArrayList<ArrayList<Double>> trails;
    private ArrayList<Double> probabilities;
    private final double alpha;
    private final double beta;
    private final double evaporationRate;
    private final int Q = 1;
    private final double nguy = 0.5;
    private final double sa_temp;
    private final double sa_coolRate;
    private final int gm_k;
    private final int gm_alpha;
    private int bestTourIndex;
    private ArrayList<Integer> bestTour;
    private double bestValue = Double.MIN_EXPONENT;

    public HybridACOSA(int numAnts, ArrayList<Machine> mc, double alpha, double beta, double evaporationRate, double sa_temp, double sa_coolRate, int gm_k, int gm_alpha) {
        pms = new ArrayList<>();
        vms = new ArrayList<>();
        for (Machine i : mc) {
            switch (i) {
                case PhysicalMachine physicalMachine ->
                    pms.add(physicalMachine);
                case VirtualMachine virtualMachine ->
                    vms.add(virtualMachine);
                default -> {
                }
            }
        }

        this.numAnts = numAnts;
        this.numVM = vms.size();
        this.numPM = pms.size();
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        trails = new ArrayList<>();
        probabilities = new ArrayList<>();
        this.sa_temp = sa_temp;
        this.sa_coolRate = sa_coolRate;
        this.gm_k = gm_k;
        this.gm_alpha = gm_alpha;
    }

    public void solve() {
        ArrayList<Ant> A = new ArrayList<>();

        for (int i = 0; i < numVM; i++) {
            ArrayList<Double> trail = new ArrayList<>();
            for (int j = 0; j < numPM; j++) {
                trail.add(0.001);
            }
            trails.add(trail);
        }

        for (int index = 0; index < 100; index++) {
            for (int index2 = 0; index2 < numAnts; index2++) {
                Ant a = generateAntTour();
                if (Double.compare(a.value, bestValue) == 0) {
                    bestTourIndex = A.size();
                }
                A.add(a);
            }

            for (int g = 0; g < 100; g++) {
                double rand = RandDouble(0, 1);
                if (rand <= 0.8) {
                    Ant a = generateSAtour(A.get(bestTourIndex), sa_temp, sa_coolRate);
                    if (Double.compare(a.value, bestValue) == 0) {
                        bestTourIndex = A.size();
                    }
                    A.add(a);
                }
            }
        }

        System.out.println("Best Solution using Hybrid ACO-SA is:" + bestTour.toString());
        System.out.println("Fairness-Utilization value is " + bestValue);
    }

    private Ant generateAntTour() {
        Ant a = new Ant();
        ArrayList<Integer> tour = new ArrayList<>();

        while (tour.size() != numVM) {
            tour = GenerateTour();
        }

        a.tour = tour;
        a.model = new GameModel(pms, vms, gm_k, gm_alpha);
        a.value = VirtualAllocation(a, tour);

        return a;
    }

    private Ant generateSAtour(Ant bestant, double temporature, double coolRate) {
        Ant ant = new Ant();
        ant.model = new GameModel(pms, vms, gm_k, gm_alpha);
        ArrayList<Integer> intialtour = new ArrayList<>();
        ArrayList<Integer> bestroute = bestant.tour;
        ArrayList<PhysicalMachine> host = CloneHost(pms);

        int rand = RandInteger(1, numVM);
        if (bestroute.size() == numVM && rand <= Math.round(numVM / 3)) {
            for (int index = 0; index < rand; index++) {
                try {
                    Allocation(host, bestroute.get(index), index);
                    intialtour.add(bestroute.get(index));
                } catch (Exception ex) {

                }
            }
        } else {
            rand = 0;
        }

        while (temporature >= 20) {
            ArrayList<Integer> tour = new ArrayList<>();
            ArrayList<PhysicalMachine> mhost = null;

            while (tour.size() != numVM) {
                tour = new ArrayList<>();
                mhost = CloneHost(host);

                for (int i = 0; i < intialtour.size(); i++) {
                    tour.add(intialtour.get(i));
                }

                for (int i = rand; i < numVM; i++) {
                    int randPM = RandInteger(0, numPM - 1);
                    randPM = CheckAvailable(mhost, randPM, i, 1);
                    if (randPM == -1) {
                        break;
                    }
                    tour.add(randPM);
                    Allocation(host, randPM, i);
                }
            }

            double bettervalue = ant.model.FairnessUtilization(mhost);

            if (Double.compare(bettervalue, bestValue) > 0) {
                bestValue = bettervalue;
                bestTour = tour;
            }

            updateTrails(host, trails, numVM, numPM);
            temporature = temporature * (1 - coolRate);
        }

        return ant;
    }

    private ArrayList<PhysicalMachine> CloneHost(ArrayList<PhysicalMachine> source) {
        ArrayList<PhysicalMachine> host = new ArrayList<>();
        for (Iterator<PhysicalMachine> it = source.iterator(); it.hasNext();) {
            PhysicalMachine pm = new PhysicalMachine(it.next());
            host.add(pm);
        }
        return host;
    }

    private ArrayList<Integer> GenerateTour() {
        ArrayList<Integer> tour = new ArrayList<>();
        ArrayList<PhysicalMachine> hosts = CloneHost(pms);

        int currentNode = 0;

        for (int i = 0; i < numVM; i++) {

            probabilities = calculateProbabilities(trails, hosts, currentNode);

            int nextNode = selectNext(currentNode, probabilities, hosts);

            if (nextNode == -1) {
                break;
            }

            hosts.get(nextNode).Allocation(vms.get(i));
            tour.add(nextNode);
            currentNode = nextNode;
        }

        updateTrails(hosts, trails, numVM, numPM);
        return tour;
    }

    private double VirtualAllocation(Ant a, ArrayList<Integer> tour) {
        ArrayList<PhysicalMachine> hosts = CloneHost(pms);

        for (int i = 0; i < tour.size(); i++) {
            Allocation(hosts, tour.get(i), i);
        }

        double value = a.model.FairnessUtilization(hosts);

        if (Double.compare(value, bestValue) > 0) {
            bestTour = tour;
            bestValue = value;

        }

        return value;
    }

    private int selectNext(int currentNode, ArrayList<Double> probabilities, ArrayList<PhysicalMachine> hosts) {
        int index = -1;
        double maxProb = 0.0;

        double r = RandDouble(0.0, 1.0);
        if (Double.compare(r, 0.6) <= 0) {
            for (int i = 0; i < numPM; i++) {
                index = RandInteger(0, numPM - 1);
                PhysicalMachine pm = hosts.get(index);
                if (pm.CheckAvailable(vms.get(currentNode))) {
                    return index;
                }
            }

        } else {
            for (int i = 0; i < numPM; i++) {
                PhysicalMachine pm = hosts.get(i);
                if (Double.compare(maxProb, probabilities.get(i)) < 0 && pm.CheckAvailable(vms.get(currentNode))) {
                    maxProb = probabilities.get(i);
                    index = i;
                }
            }
        }

        return index;
    }

    private void updateTrails(ArrayList<PhysicalMachine> hosts, ArrayList<ArrayList<Double>> trails, int numVM, int numPM) {
        double r[] = UpdatePheromone(hosts);
        double contribution = Q / (1 / (nguy * r[0] + (1 - nguy) * r[1]));
        for (int i = 0; i < numVM; i++) {
            for (int j = 0; j < numPM; j++) {
                trails.get(i).set(j, (1 - evaporationRate) * trails.get(i).get(j) + contribution);
            }
        }
    }

    private void Allocation(ArrayList<PhysicalMachine> host, int indexPM, int indexVM) {
        host.get(indexPM).Allocation(vms.get(indexVM));
    }

    private int CheckAvailable(ArrayList<PhysicalMachine> host, int indexPM, int indexVM, int runtime) {
        while (!host.get(indexPM).CheckAvailable(vms.get(indexVM))) {
            indexPM = RandInteger(0, numPM - 1);
            if (runtime > numPM * 10) {
                return -1;
            }
            runtime++;
        }
        return indexPM;
    }

    private int RandInteger(int min, int max) {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result = ThreadLocalRandom.current().nextInt(((max - min) + 1)) + min;
        }
        return result;
    }

    private double RandDouble(double min, double max) {
        double result = 0.0;
        for (int i = 0; i < 10; i++) {
            result = ThreadLocalRandom.current().nextDouble(((max - min) + 1)) + min;
        }
        return result;
    }

    private double[] UpdatePheromone(ArrayList<PhysicalMachine> hosts) {
        double[] uload = new double[numPM];
        double[] wload = new double[numPM];
        for (int i = 0; i < hosts.size(); i++) {
            uload[i] = Math.round((0.3 * (hosts.get(i).getCore() - hosts.get(i).getAvailable_core())
                    + 0.3 * (hosts.get(i).getRam() - hosts.get(i).getAvailable_ram())
                    + 0.4 * (hosts.get(i).getDisk() - hosts.get(i).getAvailable_disk())) * 100) / 100;
            wload[i] = Math.sqrt(Math.pow(hosts.get(i).getAvailable_core() / hosts.get(i).getCore(), 2)
                    + Math.pow(hosts.get(i).getAvailable_ram() / hosts.get(i).getRam(), 2)
                    + Math.pow(hosts.get(i).getAvailable_disk() / hosts.get(i).getDisk(), 2));
        }
        double[] s = new double[2];
        double avgrload = avgrLoadPM(uload);

        double varLoad = 0.0;
        double wasteLoad = 0.0;
        for (int j = 0; j < numPM; j++) {
            varLoad += Math.pow(uload[j] - avgrload, 2);
            wasteLoad += wload[j];
        }
        s[0] = varLoad / (1.0 * (numPM - 1));
        s[1] = wasteLoad;
        return s;
    }

    private double avgrLoadPM(double[] aload) {
        double avgrload = 0.0;
        for (int i = 0; i < aload.length; i++) {
            avgrload += aload[i];
        }
        return avgrload / numPM;
    }

    private ArrayList<Double> calculateProbabilities(ArrayList<ArrayList<Double>> trails, ArrayList<PhysicalMachine> hosts, int currentNode) {
        ArrayList<Double> localProbabilities = new ArrayList<>();
        double pheromone = pheromones(trails, currentNode, hosts);
        for (int i = 0; i < numPM; i++) {
            double probability = Math.pow(trails.get(currentNode).get(i), alpha) * Math.pow(1.0 / hosts.get(i).performance(), beta);
            localProbabilities.add(probability / pheromone);
        }
        return localProbabilities;
    }

    private double pheromones(ArrayList<ArrayList<Double>> trails, int currentNode, ArrayList<PhysicalMachine> hosts) {
        double pheromone = 0.0;

        for (int i = 0; i < numPM; i++) {
            pheromone += Math.pow(trails.get(currentNode).get(i), alpha) * Math.pow(1.0 / hosts.get(i).performance(), beta);
        }
        return pheromone;
    }

    private class Ant {

        ArrayList<Integer> tour;
        double value;
        GameModel model;

        Ant() {
            tour = new ArrayList<>();
            value = 0.0;
            model = null;
        }
    }
}
