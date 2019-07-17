package classify;

import java.io.*;
import java.util.*;


/**
 * @author e_e102 /Esponda
 * @since 4/4/2019
**/
public class ImageBuild {

    /**
     * Creates a Build object with default configuration.
    **/
    public ImageBuild(){
        this.imagePath = DEFAULT_IMAGE_PATH_IN;
        this.maskPath = DEFAULT_MASK_PATH_IN;
        this.modelPath = DEFAULT_MODEL_PATH_OUT;
    }

    /**
     * Creates a Build object with custom configuration. If any of the parameters are null, the default value will be
     * used. This any or none of the parameters can be specified custom.
     * @param imagePath path to the images used for training model
     * @param maskPath path to the masks used for training model
     * @param modelPath path to output the trained model file
    **/
    public ImageBuild(String imagePath, String maskPath, String modelPath){
        this.imagePath = imagePath == null ? DEFAULT_IMAGE_PATH_IN : imagePath;
        this.maskPath = maskPath == null ? DEFAULT_MASK_PATH_IN : maskPath;
        this.modelPath = modelPath == null ? DEFAULT_MODEL_PATH_OUT : modelPath;
    }


    /**
     * Gets all the files in a specified directory
     * @param folderPath path to folder for file extraction
    **/
    public String[] getAllFiles(String folderPath){
        File[] files = new File(folderPath).listFiles();

	        Arrays.sort(files, new Comparator<File>() {	       	
                @Override	     
	            public int compare(File o1, File o2) {

	                int n1 = extractNumber(o1.getName());
	                int n2 = extractNumber(o2.getName());
	                
	                return n1 - n2;
	            }

	            private int extractNumber(String name) {
	                int i = 0;
	                try {
	                    int s = name.indexOf('(')+1;
	                    int e = name.lastIndexOf(')');
	                    String number = name.substring(s, e);
	                    i = Integer.parseInt(number);	                    
	                } catch(Exception e) {
	                    i = 0; // if filename does not match the format
	                           // then default to 0
	                }
	                return i;
	            }
	        });

		ArrayList<String> filePathList = new ArrayList<String>();
		
		for(File file: files){
			filePathList.add(file.getAbsolutePath());
		}
		String[] filePaths = new String[filePathList.size()];
        filePathList.toArray(filePaths);
		
		return filePaths;
    }
    
    
    /**
     * Counts the number of skin and non-skin pixels in an image
     * @param matImage matrix representation of an image used for training
     * @param matMask matrix representation of a mask used for training
     * @param skinPixelNumber skin "positive" histogtam 
     * @param nonskinPixelNumber non-skin "negative" histogram 
    **/
    private void readSkinColor(Matrix matImage, Matrix matMask, int[][][] skinPixelNumber, int[][][] nonskinPixelnumber,int histogramSize){
		int rows = matImage.getRows();
		int cols = matImage.getCols();		
		
		int red=0,green = 0,blue = 0;
		for(int row =0,col;row<rows;row++){
			for(col=0;col<cols;col++){
				red = matImage.pixels[row][col][0];
				green = matImage.pixels[row][col][1];
                blue = matImage.pixels[row][col][2];
                if(histogramSize <= 32){
                    red = (int) Math.floor(red/8);
                    blue = (int) Math.floor(blue/8);
                    green = (int) Math.floor(green/8);
                }
				
				if(doesShowSkin(matMask.getPixel(row, col))){
					
					skinPixelNumber[red][green][blue]++;
				}else{
					nonskinPixelnumber[red][green][blue]++;
				}
			}
		}
    }

    /**
     * Trains a model that creates a 32x32x32 histogram 
    **/    
    public void train32() throws Exception{
        train(32);
    }

    /**
     * Trains a model that creates a 256x256x256 histogram 
    **/    
    public void train256() throws Exception{
        train(256);
    }

    /**
     *  Trains the model using the images and masks
     *  @param histogramSize the size of the three dimensions of the histogram
    **/
    public void train(int histogramSize) throws Exception {
        String[] imageFilePaths = getAllFiles(imagePath);
		String[] maskFilePath = getAllFiles(maskPath);
		
		Matrix matImageTemp;
        Matrix matMaskTemp;
        int dim = histogramSize;
		int[][][] skinPixelNumber = new int[dim][dim][dim];
		int[][][] nonskinPixelNumber = new int[dim][dim][dim];
		
        System.out.println("\n\n### \t\tProcessing images..."); 
        
		for(int i=0;i<imageFilePaths.length;i++){
			matImageTemp = new Matrix(imageFilePaths[i],Matrix.RED_GREEN_BLUE);
			matMaskTemp = new Matrix(maskFilePath[i],Matrix.RED_GREEN_BLUE);
			readSkinColor(matImageTemp,matMaskTemp,skinPixelNumber,nonskinPixelNumber,histogramSize);
			
		}
		System.out.println("\n\n### \t\tProcessing data...");
		caculteProbability(skinPixelNumber,nonskinPixelNumber,histogramSize);
    }

    /**
     * Checks if the mask indicates than a R G B value contains skin
     * If yes, return true. If no, return false
     * @param value an array of a pixels R G B values
    **/
    private boolean doesShowSkin(int[] value){
		if(value[0]>250 && value[1]>250 && value[2]>250){
			return true;
		}else{
			return false;
		}
	}

    /**
     * For all RGB combinations calculate the probability of the R G B value being skin
     * @param skinPixelNumber histogram of skin pixel occurences in the training set of images
     * @param nonskinPixelNumber histogram of non-skin pixel occurences in the training set of images
    **/	
    private void caculteProbability(int[][][] skinPixelNumber, int[][][] nonskinPixelNumber, int histogramSize) throws IOException {
        int totalSkinPixelNumber=0;
        int totalNonskinPixelNumber=0;

        for(int i=0; i<histogramSize; i++){
            for(int j=0; j<histogramSize; j++){
                for(int k=0; k<histogramSize; k++){
                    totalSkinPixelNumber += skinPixelNumber[i][j][k];
                 
                    totalNonskinPixelNumber += nonskinPixelNumber[i][j][k];
                }
            }
        }

        double probabilityOfSkin = (double) totalSkinPixelNumber/(totalNonskinPixelNumber+totalSkinPixelNumber);
        double probability=0;
        BufferedWriter mainBW = new BufferedWriter(new FileWriter(modelPath));

        mainBW.write("");

        for(int i=0; i<histogramSize; i++){
            for(int j=0; j<histogramSize; j++){
                for(int k=0; k<histogramSize; k++){
                                        
                    probability = skinPixelNumber[i][j][k]*probabilityOfSkin
                            /(skinPixelNumber[i][j][k]+nonskinPixelNumber[i][j][k]);
                    if(Double.isNaN(probability)) {
                        probability = 0.0;
                    }
                    
                    mainBW.append(String.format("%.3f\n", probability));
                }
            }
            
            System.out.print(".");	//notification
        }
        mainBW.close();
        System.out.println("\n\n### \t\tModel Training is complete..."); 

        return;
    }

    /**
     * @param args Command line arguments.
     * @throws Exception exceptions.
    **/
    public static void main(String[] args) throws Exception {

        ImageBuild imBuild;

        // Extract command line arguments
        String imagePath, maskPath, modelPath;

        if (args.length == 0){
            imBuild = new ImageBuild();
            imBuild.train32();
        }else if(args.length == 2){
            imagePath = args[0];
            maskPath = args[1];
            imBuild = new ImageBuild(imagePath,maskPath,null);
            imBuild.train32();
        }else if(args.length == 3){
            imagePath = args[0];
            maskPath = args[1];
            modelPath = args[2];
            imBuild = new ImageBuild(imagePath,maskPath,modelPath);
            imBuild.train32();
        }else{
            throw new IllegalArgumentException();
        }
   
    }

    private static final String DEFAULT_IMAGE_PATH_IN = "../../skinDetector/train/images";
	private static final String DEFAULT_MASK_PATH_IN = "../../skinDetector/train/mask";
	private static final String DEFAULT_MODEL_PATH_OUT = "../lib/images_knowledge.dat";

    private String imagePath;
    private String maskPath;
    private String modelPath;

}
