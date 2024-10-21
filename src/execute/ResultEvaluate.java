package execute;

import algorithm.GameModel;
import definition.Machine;
import definition.PhysicalMachine;
import definition.VirtualMachine;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TrongDai
 */
public class ResultEvaluate {
    
    public ResultEvaluate(ArrayList<Machine> machine) {
        ArrayList<PhysicalMachine> pms = new ArrayList<>();
        ArrayList<VirtualMachine> vms = new ArrayList<>();
        for (Machine i : machine) {
            switch (i) {
                case PhysicalMachine p -> pms.add(p);
                case VirtualMachine v -> vms.add(v);
                default -> {
                }
            }
        }
        
        ArrayList<Integer> tour = new ArrayList<>(List.of(
           15, 26, 112, 68, 55, 32, 84, 67, 82, 113, 117, 63, 37, 45, 97, 23, 31, 59, 20, 64, 58, 4, 21, 4, 103, 62, 30, 54, 73, 64, 78, 58, 104, 28, 32, 113, 33, 83, 58, 52, 64, 81, 17, 50, 14, 112, 64, 61, 87, 99, 86, 12, 84, 57, 70, 99, 59, 67, 23, 28, 72, 53, 37, 100, 3, 7, 32, 16, 28, 98, 39, 89, 41, 44, 78, 9, 87, 8, 51, 59, 10, 118, 38, 16, 42, 82, 88, 23, 91, 9, 2, 66, 80, 100, 85, 17, 86, 106, 71, 78, 48, 62, 83, 74, 4, 71, 118, 7, 19, 47, 55, 61, 108, 17, 53, 83, 38, 39, 68, 22, 9, 46, 0, 10, 111, 119, 52, 91, 87, 103, 98, 44, 11, 20, 11, 79, 24, 6, 69, 41, 108, 114, 110, 22, 18, 47, 63, 1, 45, 58, 58, 45, 7, 89, 110, 57, 92, 51, 34, 67, 54, 26, 53, 35, 25, 42, 72, 71, 99, 95, 42, 97, 60, 7, 82, 89, 111, 90, 60, 74, 23, 13, 56, 35, 113, 22, 86, 109, 39, 14, 39, 47, 31, 103, 12, 83, 66, 92, 117, 96, 70, 51, 24, 11, 13, 23, 10, 114, 37, 47, 76, 46, 47, 3, 33, 39, 8, 94, 94, 11, 40, 61, 97, 114, 23, 0, 20, 81, 101, 90, 40, 107, 109, 19, 19, 10, 64, 57, 72, 41, 29, 52, 64, 44, 16, 51, 56, 66, 115, 30, 44, 64, 75, 22, 39, 70, 97, 2, 77, 35, 110, 67, 104, 23, 11, 105, 8, 64, 79, 80, 46, 81, 93, 74, 108, 114, 58, 23, 84, 86, 0, 9, 84, 36, 22, 18, 57, 8, 116, 85, 17, 116, 85, 37, 111, 22, 47, 92, 21, 65, 96, 106, 113, 87, 3, 103, 8, 0, 76, 85, 98, 27, 17, 55, 90, 53, 119, 44, 15, 18, 56, 100, 61, 32, 84, 81, 21, 2, 9, 119, 61, 21, 118, 95, 38, 71, 88, 108, 26, 61, 89, 31, 91, 22, 36, 17, 8, 89, 24, 34, 38, 29, 17, 115, 88, 112, 4, 119, 109, 105, 14, 81, 110, 92, 2, 99, 17, 108, 38, 13, 102, 106, 71, 5, 42, 28, 98, 28, 92, 18, 102, 20, 68, 86, 22, 38, 26, 19, 104, 29, 115, 54, 14, 98, 10, 87, 100, 30, 62, 60, 33, 20, 75, 118, 7, 62, 51, 23, 119, 72, 90, 101, 119, 76, 48, 3, 92, 80, 27, 6, 55, 66, 4, 27, 15, 15, 43, 16, 27, 107, 27, 119, 54, 8, 1, 52, 53, 61, 119, 97, 76, 104, 44, 56, 104, 50, 33, 56, 60, 73

        ));
        
        ArrayList<VirtualMachine> v = new ArrayList<>();
        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        for (int i = 0; i < tour.size(); i++) {
            v.add(vms.get(i));
            pms.get(tour.get(i)).Allocation(vms.get(i));
            
            GameModel model = new GameModel(pms, v, 3, 2);
            list1.add(model.getOmega());
            list2.add(model.ResourceUtilization(pms));
        }
        
        System.out.println(list1.toString());
        System.out.println(list2.toString());
        
    }
}
