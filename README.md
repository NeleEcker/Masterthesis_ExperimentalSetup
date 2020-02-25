# Masterarbeit

# Experimental Setup
## Creation of negative fact set

### DBpedia

To create the negative sample set for DBpedia one first has to download two versions
of the data set. In this work the data sets were downloaded from the official
DBpedia download websites: 

http://downloads.dbpedia.org/2016-04/core-i18n/en/mappingbased_objects_en.ttl.bz2,
http://downloads.dbpedia.org/2016-10/core-i18n/en/mappingbased_objects_en.ttl.bz2


After the download the Java program NegSamplesDBpedia.java can be executed
to determine the negative facts. Within the code the file paths leading to the
downloaded data sets and the desired path to store the model containing the true
negative facts need to be adapted.

### NELL
To create the negative fact set for NELL one first has to download all human feedback
sheets from its website: http://rtw.ml.cmu.edu/rtw/resources. The human
feedback sheets contain the negative facts. The positive facts are extracted from
the model that can be downloaded from the NELL archive: http://wdaqua-nell2rdf.univ-st-etienne.fr/archive/NELL2RDF_0.3_1100.nt.gz

To determine the negative facts only the feedback sheets are necessary. Save
them in one directory only containing these files. The Java program NegSamplesNell.
java can be used to extract the negative facts from all feedback sheets.
The path to the directory containing all feedback sheets needs to be adapted. The
list with all extracted negative facts is stored in the same directory.

## Data transformation to OpenKE format

To be able to use the knowledge bases within the OpenKE framework it is necessary
to transform them into the data sets with following the predefined structure.
As the negative fact set is in different formats for DBpedia and NELL, the code to
transform the data is split into different programs. Next to the transformation of
the data, this section contains information about how to create the samples of the
data sets as this is closely connected to the transformation.

### DBpedia
This section contains the description of how DBpedia can be transformed and how
the sample can be created.

The transformation can be done executing the file TransformDBpedia.java.
The following empty files need to be created before running the program for the
first time. Their paths need to be specified in the code as well:

• train2id.txt

• valid2id.txt

• test2id.txt

• entity2id.txt

• relation2id.txt

• diff2id.txt

The files need to have exactly these names as the framework has specified them
as import files.

Furthermore, the path to the newer version of the model that has been downloaded
to create the negative fact set as well as the path to the model containing the
negative facts need to be adapted.

The run time of the program is quite long as the loading of the model requires
some time.

The sample creation of DBpedia is split into multiple steps. 

First the sample set needs to be downloaded from: https://drive.google.com/drive/folders/1YBKw4nOnbscpDeTD_gWxfcpHRFG3MY20. 

The file structure as required for the OpenKE framework is already there though the entities and relations are shown with their names
instead of keys. Executing the Java program ChangeNameToKey.java transforms
the names of the entities and relations. Only the first path variable in the code needs
to be adapted pointing to the folder in which the sample files are in.

Finally, the negative fact set has to be combined with the sample. This can be
done executing the Java program CombineSampleWithNegs.java. Adapt the paths
in the code containing the information about the entities and relations. The files
ending with ”new” have to be created before running the code for the first time. In
case that the number of entities and relations shall not be adapted the files ending
with ”new” show the same entities and relations as the input files.
### NELL
The transformation of NELL as well as the sample creation of NELL are done
in the same file TransformAndSampleNell.java. This is possible as no existing
sample is taken. Create the same files as for DBpedia and make sure they are
named correctly, so they can be further used by the framework. Furthermore, the
path to the model of NELL that has been downloaded in the previous step as well
as the path to file containing all negative facts of NELL need to be specified.
The sample creation is done in the method readTrueSamples. To change the
sample that is created uncomment lines and comment others.


In case no sample of NELL shall be created the Java program Transform-
Nell.java can be executed. The same paths have to be adapted as for the program
TransformAndSampleNell.java.

# Analyses
Two further analyses were performed in the Masterthesis. This section explains which code was used to perform the analyses and how it can be reused.
## Negative fact set analysis
Random negative facts can be extracted from DBpedia and NELL executing the
Java program RandomSampleNegEntities.java. Also the paths need to adapted.
For DBpedia the path to the Difference Model needs to be determined, pointing to
the output file of the negative fact creation of DBpedia. Furthermore, the paths to
the files entity2id.text and relation2id.txt are required. For NELL the path pointing
to the file containing all negative facts of NELL is required. For both cases one has
to determine where the output files shall be stored.
## Hits@10 analysis
The second analysis takes a closer look at the Hits@10 results. The results printed
into the console after testing are showing only keys of the entities. Executing the
Java program ChangeKeyToName.java these keys are transformed into the entity
names which are necessary for the analysis. Three paths need to be specified.

First the path to the file entity2id.txt of the respective data set as well as the path
to the file containing all predictions. The third path determines the file that the
transformed prediction results shall be stored in.
The elements in the input prediction file need to be separated by blank spaces.
This can be easily achieved by copying the prediction result from the console into
a file and removing the brackets.
