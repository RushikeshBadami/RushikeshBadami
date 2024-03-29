import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 
 */

/**
 * @author
 *
 */
public class nbtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, Probability> prob = new LinkedHashMap<String, Probability>();
		try {
			prob = getdata(prob, args[0]);
			classifyDocument(args[1], prob, args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the data from model file
	 * 
	 * @param prob the probability map
	 * @param path the path of the model file
	 * @return the probabilities map
	 * @throws IOException when error occurs
	 */
	public static HashMap<String, Probability> getdata(HashMap<String, Probability> prob, String path)
			throws IOException {
		// reader for reading the file
		BufferedReader inputStream = null;
		inputStream = new BufferedReader(new FileReader(path));
		String line;
		Probability probability = null;
		// iterate over the model file
		while ((line = inputStream.readLine()) != null) {
			String tokens[] = line.split(" ");
			prob.put(tokens[0], new Probability(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));
		}

		// return the map of probability
		return prob;

	}

	/**
	 * Method to classify the document
	 *  
	 * @param inputDir the input file for the test
	 * @param prob the probability map
	 * @param predicitionFile the prediction file path
	 * @throws IOException when error occurs
	 */
	public static void classifyDocument(String inputDir, HashMap<String, Probability> prob, String predicitionFile) throws IOException {
		// writer for writing the prediction
		BufferedWriter writer=new BufferedWriter(new FileWriter(predicitionFile));
		File dir = new File(inputDir);
		File[] files = dir.listFiles();
		// the prior probability
		Double priorProbability = 0.5;
		// iterate over each file
		for (File f : files) {
			if (f.isFile()) {
				BufferedReader inputStream = null;
				inputStream = new BufferedReader(new FileReader(f));
				String line;
				double pos = 0.0;
				double neg = 0.0;
				// read file line by line
				while ((line = inputStream.readLine()) != null) {
					String tokens[] = line.split(" ");
					for (int i = 0; i < tokens.length; i++) {
						if (prob.containsKey(tokens[i])) {
							Probability proba = prob.get(tokens[i]);
							pos = pos + proba.getPos();
							neg = neg + proba.getNeg();
						}
					}
				}
				
				pos = pos + Math.log(priorProbability);
				neg = neg + Math.log(priorProbability);
				
				// classify the file on basis of the positive and negative score
				if (pos > neg) {
					writer.write("File:" + f.getName() + " " + pos + " " + neg + " Positive\n");
					System.out.println("File:" + f.getName() + " Positive");
				} else {
					writer.write("File:" + f.getName() + " " + pos + " " + neg + " Negative\n");
					System.out.println("File:" + f.getName() + " Negative");
				}
			}
		}
		
		// close writer
		writer.close();
	}
	
}
