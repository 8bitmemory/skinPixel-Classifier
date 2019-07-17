package classify;

import java.io.File;
public class Driver{

public static void main(String[] args) {

if(args.length != 2 || !args[0].equals("i") && !args[0].equals("d")) { 
System.out.println("Usage: <processing flag> <Path to image(s)>\n\n"+
"processing flag: single character denoting whether\n"+ 
"to process a single image or a directory of images.\n"+
"'i': single image.\n"+
"'d': directory of images.\n"+
"Path to images: The fully qualified path from the root\n"+
"to the image/directory of interest.\n" +
"Current directory can be found with the pwd and chdir\n"+
"commands in Linux and Windows respectively.\n"
);
System.exit(1);
}

if(args[0].equals("i")) {
	
	String testImagePath = args[1];

	String knowledgePath = new File("").getAbsolutePath();
	knowledgePath = knowledgePath + "/images_knowledge.dat";
		
	try {
		SkinDetectorTester.recogniseImage(testImagePath, knowledgePath);
		System.out.println("\n\n## \t\tSuccessful!!");
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println("\n\n## \t\t File Path Error!!");
		System.out.println("Given Path: " + testImagePath);
				}
}else if(args[0].equals("d")) {

	String knowledgePath = new File("").getAbsolutePath();
	knowledgePath = knowledgePath + "/images_knowledge.dat";

	String [] files = SkinDetectorTester.getAllFiles(args[1]);
	for(int i = 0; i < files.length; i++) {
		String testImagePath = files[i];

		try {
			SkinDetectorTester.recogniseImage(testImagePath, knowledgePath);
			System.out.println("\n\n## \t\tSuccessful!!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n\n## \t\t File Path Error!!");
		}
	}
		
}
}





}
