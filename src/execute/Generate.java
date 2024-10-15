package execute;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author TrongDai
 */
public class Generate {

    public static void main(String[] args) {
        String path1 = "pm.txt", path2 = "vm.txt";
        int pm = 500, vm = 2000;
        int[] gpcpu = {64, 128, 256, 512};
        int[] gpram = {128, 256, 512, 1024};
        int[] gpdisk = {2048, 4096, 8192, 16384};
        int[] gvcpu = {2, 4, 8, 16, 32};
        int[] gvram = {2, 4, 8, 16, 32, 64};
        ArrayList<String> array1 = new ArrayList<>();
        ArrayList<String> array2 = new ArrayList<>();
        
        for (int i = 0; i < pm; i++) {
            String m = "";
            m = m + gpcpu[RandomIntMinMax(0, 3)] + " " 
                    + gpram[RandomIntMinMax(0, 3)] + " " 
                    + gpdisk[RandomIntMinMax(0, 3)];
            array1.add(m);
        }
        
        for (int i = 0; i < vm; i++) {
            String m = "";
            int rd = RandomIntMinMax(4, 100) * 10;
            m = m + gvcpu[RandomIntMinMax(0, 4)] + " " 
                    + gvram[RandomIntMinMax(0, 5)] + " " 
                    + rd;
            array2.add(m);
        }
        
        WriteFile(array1, path1);
        WriteFile(array2, path2);
//        System.out.println(array1.toString());
//        System.out.println(array2.toString());
    }

    private static int RandomIntMinMax(int min, int max) {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result = ThreadLocalRandom.current().nextInt(((max - min) + 1)) + min;
        }
        return result;
    }
    
    private static void WriteFile(ArrayList<String> array, String path) {
        try {
            FileWriter writer = new FileWriter(path);

            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for (String value : array) {
                bufferedWriter.write(value);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();

            System.out.println("Dữ liệu đã được ghi vào file thành công.");
        } catch (IOException e) {
            System.err.println("Đã xảy ra lỗi khi ghi vào file: " + e.getMessage());
        }
    }
}
