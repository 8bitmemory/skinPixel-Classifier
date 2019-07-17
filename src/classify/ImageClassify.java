package classify;

import java.io.*;
import java.util.*;

/**
 * @author Stence, Lambert, Knapper, Esponda
 * @since Apr 26 2019
**/
public class ImageClassify{

    /**
     * Creates a Classify object and loads the model included in the JAR.
     * @throws Exception
    **/
    public ImageClassify() throws Exception {
        this.modelPath = DEFAULT_MODEL_PATH;
    }

    /**
     * Creates a classify object and allows the user to specify their own model path.
     * @param modelPath The path of the .model file to use.
     * @throws Exception
    **/
    public ImageClassify(String modelPath) throws Exception {
        this.modelPath = modelPath;
    }

    /**
     * Returns the path of the model file in use.
     * @return The path of the model file in use.
    **/
    public String getModelPath() {
        return this.modelPath;
    }

    /**
     * Classifies the sentiment of text as positive, negative, or neutral.
     * @param imagePath The imagePath
     * @return An array of doubles containing the confidence that the input text belongs to each class.
     * @throws Exception exceptions
     * @author Josh
     */
    //public double predict(String imagePath) throws Exception {
      //checks imagePath
      // calls predictSkin
      //computers percentage from returned Matrix object
    //}

    /**
     * Classifies the sentiment of text as positive, negative, or neutral.
     * @param imagePath The imagePath
     * @param jsonPath JSON Path
     * @return An array of doubles containing the confidence that the input text belongs to each class.
     * @throws Exception exceptions
     * @author Josh
     */
    //public double predict(String imagePath, String jsonPath) throws Exception {

      //checks imagePath
      //checks JSON path - > if correct gets face bounding box (e.g. instantiate new Matrix object)
      // calls predictSkin
      //Filter operation on returned Matrix object + new Matrix object from face bounding box
      //return new skin percentage
    //}

    /**
     * @param modelPath
     * @return A 3d array with the proboabilities of RGB combinations being skin
     * @throws IOExeption
     * @author Elliott, James
    **/
    private double[][][] getData(String modelPath, int size) throws IOException{
		String string = "";
        int Num = size;
        BufferedReader newBR = new BufferedReader(new FileReader(modelPath));
        double[][][] probabiltyMatrix = new double [Num][Num][Num];
        
		System.out.println("## \t\tReading Knowledge File...");
		for(int i=0; i<Num; i++){
			for(int j=0; j<Num; j++){
				for(int k=0; k<Num; k++){
					string = newBR.readLine();
					probabiltyMatrix[i][j][k] = Double.parseDouble(string);
				}
			}
			System.out.print(".");	//notification
		}
		newBR.close();
		return probabiltyMatrix;
	}
    /**
     * Classifies skin pixels as skin or non-skin pixels
     * @param imagePath path to image
     * @return Matrix object
     * @throws Exception exceptions.
     * This function calls the original predictSkin function with a default threshold of .04 if 
     * one is not provided by the user.
     * @authors Elliot, James
    **/
    public Matrix predictSkin(String imagePath) throws Exception {
        Matrix matMask = predictSkin(imagePath, .04);
        return matMask;
    }

    /**
     * Classifies skin pixels as skin or non-skin pixels
     * @param imagePath path to image
     * @param Threshold threshold for .dat file, if none passed will default to .04
     * @return Matrix object
     * @throws Exception exceptions.
     * @authors Elliot, James
    **/
    public Matrix predictSkin(String imagePath, double threshold) throws Exception {
      
        String modelPath = getModelPath();
        BufferedReader mainBR = new BufferedReader(new FileReader(modelPath));
        int Num = 32; //dimensions default to 32 
        int lines = 0;

        while(mainBR.readLine() != null)  {
            lines++;
            if(lines > 32768) {
                Num = 256; //total size of histogram is greater than dimension 32
                break;
            }
        }

        mainBR.close();
        
        double [][][] probabiltyMatrix = getData(modelPath, Num);
        Matrix matImage = new Matrix(imagePath, Matrix.RED_GREEN_BLUE);
        Matrix matMask = new Matrix(matImage.getRows(),matImage.getCols(),Matrix.BLACK_WHITE);

        System.out.println("\n\n## \t\tProcessing Image...");
        int rows = matImage.getRows();
        int cols = matImage.getCols();
        int red=0,green = 0,blue = 0;
        for(int row=0, col; row<rows; row++){
            for(col=0; col<cols; col++){
                red = matImage.pixels[row][col][0];
                green = matImage.pixels[row][col][1];
                blue = matImage.pixels[row][col][2];

                if(Num == 32) {
                    red = (int) Math.floor(red/8);
                    blue = (int) Math.floor(blue/8);
                    green = (int) Math.floor(green/8);
                }
                
                if(probabiltyMatrix[red][green][blue] > threshold){
                    matMask.pixels[row][col][0] = 255;
                }else{
                    matMask.pixels[row][col][0] = 0;
                }
            }
        }
        return matMask;
    }

    /**
     * @param args Command line arguments.
     * @throws Exception exceptions.
    **/
    public static void main(String[] args) throws Exception {
        ImageClassify imClassify;

        // Extract command line arguments
        String modelPath, imagePath;

        if (args.length == 1){
            imagePath = args[0];
            imClassify = new ImageClassify();
            Matrix tesMatrix = imClassify.predictSkin(imagePath,.04);
            tesMatrix.write("testMask.png");
        }else if(args.length == 2){
            modelPath = args[0];
            imagePath = args[1];
            imClassify = new ImageClassify(modelPath);
            Matrix tesMatrix = imClassify.predictSkin(imagePath);
            tesMatrix.write("testMask.png");
        }else{
            throw new IllegalArgumentException();
        }
    }

    private static final String DEFAULT_MODEL_PATH = "../lib/images_knowledge.dat";
    private String modelPath;
}
