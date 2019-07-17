
From IMA/src folder, after compiling relevant java classes,  
Build jar with: jar cvfe IMA.jar classify/Driver classify/*.class

Image Analys Overview
Classifies image or image directory into pre-set class values (e.g. photo-dir repository)
Builds a common classifier from a set of images and directories

ImageClassify Confusion Matrix for 32x32x32 bin histogram:

| n = 1158327    | predicted skin | predicted nonskin |
|----------------|----------------|-------------------|
| actual skin    | 456467         | 47371             |
| actual nonskin | 34471          | 620018            |

Accuracy for detecting skin pixels (32x32x32 bin histogram): 90.6% <br/>
Accuracy for detecting nonskin pixels (32x32x32 bin histogram): 94.73%


ImageClassify Confusion Matrix for 256x256x256 bin histogram:

| n = 1158327    | predicted skin | predicted nonskin |
|----------------|----------------|-------------------|
| actual skin    | 425992         | 77846             |
| actual nonskin | 28445          | 626044            |

Accuracy for detecting skin pixels (256x256x256 bin histogram): 85.44%  <br/>
Accuracy for detecting nonskin pixels (256x256x256 bin hostogram): 95.65% <br/> <br/>


To run this code, you must instantiate an ImageBuild object.  If you pass it no parameters, it will <br/>
use the default model path to the .dat file outputted in the lib directory.  You can also pass it an <br/>
image path and a mask path, with the third argument as null.  This will use a personalized path to <br/>
your training images and training masks, with the default path to your .dat file. Additionally, you can <br/>
include a third parameter as a model path to output the .dat file where you specify. 
To train this model, you must specify calling either the train32() or train256() function located. <br/>
in the ImageBuild class. <br/>
Based off of which train function called, the model will produce either a 32x32x32 probability <br/>
histogram or a 256x256x256 probability histogram.  Calling the predict skin function from <br/>
ImageClassify will then utilize the appropriate probability histogram to detect skin pixels in <br/>
color images.  <br/>

To use the predictSkin function in ImageClassify, you must provide it with a path to a color image. <br/>
Additionally, you can pass it a threshold as a second argument.  The threshold is used to determine <br/>
over what percentage probabilty do you classify a pixel to be skin from the probability histogram. <br/>
If no argument is passed, it will default to .04, which was used to obtain the results above.