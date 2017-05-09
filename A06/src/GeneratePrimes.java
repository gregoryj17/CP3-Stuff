import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

public class GeneratePrimes {

	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("in.txt"));
		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			pw.println(new BigInteger(3 + i, 500, rand).multiply(new BigInteger(3 + i, 500, rand)));
		}
		pw.flush();
		pw.close();
	}
}
