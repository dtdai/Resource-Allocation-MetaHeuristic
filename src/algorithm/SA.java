package algorithm;

import definition.Machine;
import definition.VirtualMachine;
import definition.PhysicalMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author TrongDai
 */
public class SA {

    private final ArrayList<PhysicalMachine> pms;
    private final ArrayList<VirtualMachine> vms;
    private final int numPM;
    private final int numVM;
    private double temperature;
    private final double coolingRate;
    private final int gm_k;
    private final int gm_alpha;
    private ArrayList<Integer> besttour = new ArrayList<>();
    private double bestValue = Double.MIN_EXPONENT;

    public SA(ArrayList<Machine> machine, double temp, double coolRate, int gm_k, int gm_alpha) {
        this.pms = new ArrayList<>();
        this.vms = new ArrayList<>();
        for (Machine i : machine) {
            switch (i) {
                case PhysicalMachine p -> this.pms.add(p);
                case VirtualMachine v -> this.vms.add(v);
                default -> {
                }
            }
        }
        this.numPM = pms.size();
        this.numVM = vms.size();
        this.temperature = temp;
        this.coolingRate = coolRate;
        this.gm_k = gm_k;
        this.gm_alpha = gm_alpha;
    }

    public void solve() {
        ArrayList<Annealing> annealings = new ArrayList<>();
        while (temperature > 20) {
            Annealing a = new Annealing();
            a.model = new GameModel(pms, vms, gm_k, gm_alpha);
            ArrayList<Integer> tour = new ArrayList<>();

            while (tour.size() != numVM) {
                tour = GenerateTour();
            }

            a.tour = tour;
            a.val = VirtualAllocation(a, tour);
            annealings.add(a);

            temperature = temperature * (1 - coolingRate);
        }

        System.out.println("Best Solution using SA is: " + besttour.toString());
        System.out.println("Fairness-Utilization value is " + bestValue);
    }

    private ArrayList<Integer> GenerateTour() {
        ArrayList<Integer> tour = new ArrayList<>();
        ArrayList<PhysicalMachine> hosts = CloneHost();

        for (int i = 0; i < numVM; i++) {
            int randPM = RandInteger(0, numPM - 1);
            randPM = CheckAvailable(hosts, randPM, i, 1);
            if (randPM == -1) {
                break;
            }
            tour.add(randPM);
            Allocation(hosts, randPM, i);
        }

        return tour;
    }

    private double VirtualAllocation(Annealing a, ArrayList<Integer> tour) {
        ArrayList<PhysicalMachine> hosts = CloneHost();

        for (int i = 0; i < tour.size(); i++) {
            Allocation(hosts, tour.get(i), i);
        }

        double value = a.model.FairnessUtilization(hosts);

        if (Double.compare(value, bestValue) > 0) {
            besttour = tour;
            bestValue = value;
        }

        return value;
    }

    private ArrayList<PhysicalMachine> CloneHost() {
        ArrayList<PhysicalMachine> host = new ArrayList<>();
        for (Iterator<PhysicalMachine> it = pms.iterator(); it.hasNext();) {
            PhysicalMachine pm = new PhysicalMachine(it.next());
            host.add(pm);
        }
        return host;
    }

    private int RandInteger(int min, int max) {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result = ThreadLocalRandom.current().nextInt(((max - min) + 1)) + min;
        }
        return result;
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

    private void Allocation(ArrayList<PhysicalMachine> host, int indexPM, int indexVM) {
        host.get(indexPM).Allocation(vms.get(indexVM));
    }

    private class Annealing {

        ArrayList<Integer> tour;
        double val;
        GameModel model;

        Annealing() {
            tour = new ArrayList<>();
            val = 0.0;
            model = null;
        }
    }
}
