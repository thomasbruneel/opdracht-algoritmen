package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws IOException, ParseException {
		String inputFile=args[0];			//2_10_100_4_TRUE_65_50_50.json
		String outputFile=args[1];			//output.csv
		
		Long begin = System.currentTimeMillis();
		
		Problem1 p1 = Problem1.fromJson(new File(inputFile));
		Problem2 p2 = Problem2.fromJson(new File(inputFile));

		System.out.println("Start solve");
		ArrayList<Move> solution=new ArrayList<Move>();
		if(p2.getGantries().size()==1){
			System.out.println("1 kraan");
			solution=p1.solve();
		}
		else{
			System.out.println("2 kranen");
			solution=p2.solve();
		}
        
		BufferedWriter bw=new BufferedWriter(new FileWriter(outputFile));
		bw.write("\"gID\";\"T\";\"x\";\"y\";\"itemInCraneID\"");
        try {
            for (Move m : solution) {
                bw.write("\n");
                bw.write(m.toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        bw.close();

        Long einde = System.currentTimeMillis();
        System.out.println("Tijd verstreken:" + (einde-begin));

	}

}
