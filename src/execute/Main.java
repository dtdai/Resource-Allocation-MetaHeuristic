package execute;

import definition.Machine;
import definition.VirtualMachine;
import definition.PhysicalMachine;
import java.util.ArrayList;
import algorithm.ACO;
import algorithm.GameModel;
import algorithm.HybridPSOSA;
import algorithm.PSO;
import algorithm.SA;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author TrongDai
 */
public class Main {

    public static void main(String[] args) throws IOException {

//        <editor-fold desc="Variables">
        int numPM = 120; // Amount Physical Machine - Host | (4-10) (12-50)
        int numVM = 450; // Amount Virtual Machine - Task
        int solution = 8; // for Switch condition: 0 -> Nothing; 1 -> ACO; 2 -> PSO; 3 -> SA;
        int aco_numAnts = 100; // ACO - Amount ants generate
        double aco_alpha = 1.0; // ACO - Pheromone importance
        double aco_beta = 2.0; // ACO - Distance priority
        double aco_evRate = .5; // ACO - Pheromone evaporation rate
        int pso_numParticles = 50; // PSO - Amount Particles generate
        int pso_Iteration = 100; // PSO - Max PSO Iterator
        double pso_defaultW = 0.729844; // PSO Constants
        double pso_defaultC1 = 1.496185; // PSO Constants
        double pso_defaultC2 = 1.496185; // PSO Constants
        double sa_temp = 1000; // SA - Intialize temperature
        double sa_coolRate = .05; // SA - Cooling Rate
        int gm_k = 3; // Model - Nums resource
        int gm_alpha = 2; // Model - Coefficient
        ArrayList<Machine> mc = new ArrayList<>(); // hosts & tasks list

//        </editor-fold>

//        <editor-fold defaultstate="collapsed" desc="Testing 1">
//        mc.add(new PhysicalMachine(8, 16, 80));
//        mc.add(new PhysicalMachine(8, 12, 100));
//        mc.add(new PhysicalMachine(8, 8, 60));
//        mc.add(new PhysicalMachine(8, 8, 100));
//
//        mc.add(new VirtualMachine(4, 8, 40));
//        mc.add(new VirtualMachine(2, 2, 20));
//        mc.add(new VirtualMachine(4, 4, 20));
//        mc.add(new VirtualMachine(6, 4, 60));
//        mc.add(new VirtualMachine(4, 8, 40));
//        mc.add(new VirtualMachine(2, 6, 40));
//        ArrayList<PhysicalMachine> pm = new ArrayList<>();
//        pm.add(new PhysicalMachine(8, 16, 80));
//        pm.add(new PhysicalMachine(8, 12, 100));
//        
//        ArrayList<VirtualMachine> vm = new ArrayList<>();
//        vm.add(new VirtualMachine(4, 8, 40));
//        vm.add(new VirtualMachine(2, 2, 20));
//        vm.add(new VirtualMachine(4, 4, 20));
//        
//        pm.get(0).Allocation(vm.get(0));
//        pm.get(1).Allocation(vm.get(1));
//        pm.get(1).Allocation(vm.get(2));
//
//        GameModel gm = new GameModel(pm, vm, 3, 2);
//        System.out.println(gm.getOmega());
//        System.out.println(gm.FairnessUtilization(pm));
//        </editor-fold>

//        <editor-fold desc="Main">
        ImportFile(mc, "pm.txt", numPM, true);
        ImportFile(mc, "vm.txt", numVM, false);

        long startTime = System.nanoTime();
        
        switch (solution) {
            case 1 -> {
                // Ant Colony Optimization Algorithm
                ACO aco = new ACO(aco_numAnts, mc, aco_alpha, aco_beta, aco_evRate, gm_k, gm_alpha);
                aco.solve();
            }
            case 2 -> {
                // Particle Swarm Optimization Algorithm
                PSO pso = new PSO(mc, pso_numParticles, pso_Iteration, pso_defaultW, pso_defaultC1, pso_defaultC2, gm_k, gm_alpha);
                pso.solve();
            }
            case 3 -> {
                // Simulated Annealing Algorithm
                SA sa = new SA(mc, sa_temp, sa_coolRate, gm_k, gm_alpha);
                sa.solve();
            }
            case 4 -> {
                // Hybrid ACO-SA Algorithm

            }
            case 5 -> {
                // Hybrid PSO-SA Algorithm
                HybridPSOSA hybridPSOSA = new HybridPSOSA(mc, pso_numParticles, pso_Iteration, pso_defaultW, pso_defaultC1, pso_defaultC2, sa_temp, sa_coolRate, gm_k, gm_alpha);
                hybridPSOSA.solve();
            }
            case 8 -> {
                ResultEvaluate evaluate = new ResultEvaluate(mc);
            }
            default -> {
            }
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInMillis = duration / 1_000_000.0;
        System.out.println("Duration: " + durationInMillis + " ms.");

//        </editor-fold>

//        <editor-fold desc="Testing 2">
//        ArrayList<PhysicalMachine> pms = new ArrayList<>();
//        ArrayList<VirtualMachine> vms = new ArrayList<>();
//        ArrayList<VirtualMachine> vmps = new ArrayList<>();
//        ArrayList<Double> omp = new ArrayList<>();
//        for (Machine i : mc) {
//            if (i instanceof PhysicalMachine) {
//                pms.add((PhysicalMachine) i);
//            } else if (i instanceof VirtualMachine) {
//                vms.add((VirtualMachine) i);
//            }
//        }
//
//        for (int i = 0; i < numVM; i++) {
////            vmps.add(vms.get(i));
//            if (i % 5 == 0) {
//                vmps.add(vms.get(i));
////                continue;
////            }
//            GameModel gameModel = new GameModel(pms, vmps, gm_k, gm_alpha);
//            omp.add(gameModel.getOmega());
//            }
//        }
//        System.out.println(omp.size());
//        System.out.println(omp.toString());
//        </editor-fold>
    }

    public static void ImportFile(ArrayList<Machine> mc, String path, int num, Boolean pm) throws IOException {
        try {
            FileReader fileReader = new FileReader(path);

            ArrayList<String> lines;
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                lines = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
            }

            for (int i = 0; i < num; i++) {
                String[] values = lines.get(i).trim().split("\\s+");
                int[] arr = new int[values.length];
                for (int j = 0; j < values.length; j++) {
                    arr[j] = Integer.parseInt(values[j]);
                }
                if (pm) {
                    mc.add(new PhysicalMachine(arr[0], arr[1], arr[2]));
                } else {
                    mc.add(new VirtualMachine(arr[0], arr[1], arr[2]));
                }
            }

        } catch (IOException e) {
            System.err.println("Error from reading file: " + e.getMessage());
        }
    }
}
