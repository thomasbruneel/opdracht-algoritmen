package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws IOException, ParseException {
		String inputFile="1_10_100_4_FALSE_65_50_50.json";
		String outputFile="output.csv";
		
		Long begin = System.currentTimeMillis();
		
		Problem p = Problem.fromJson(new File(inputFile));
		ArrayList<Move> solution = p.solve();
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
