#!/usr/bin/env python

#NOTE: This file needs to be run in the input directory and has been modified from original source for perfumebox, mobilephone, and shoe examples.

import sys
import os
import os.path

def main():

  TRAIN_TEXT_FILE = 'train.txt'
  VAL_TEXT_FILE = 'val.txt'

  #Array of object names. Names are listed according in order of ascending category number. So, first item is category 0, second is category 1, etc.
  #Pictures of objects must be placed in folders bearing name of that number (first item pics in folder called 0, etc.)
  objects=[]
  objects.append("mobilephone")
  objects.append("shoe")
  objects.append("perfumebox")

  #Training image folder. Place all training images in here.
  BASE_TRAINING_IMAGE_FOLDER = 'train'

  #Validation image folder. Place all images used for validation in here.
  BASE_VALIDATION_IMAGE_FOLDER = 'validate'

  fr = open(TRAIN_TEXT_FILE, 'w')
  fv = open(VAL_TEXT_FILE, 'w')

  objectsLen=len(objects)
  #For each item in the objects array...
  for i in range(0,objectsLen):
    TRAINING_IMAGE_FOLDER=BASE_TRAINING_IMAGE_FOLDER + "/" + str(i)
    VALIDATION_IMAGE_FOLDER=BASE_VALIDATION_IMAGE_FOLDER + "/" + str(i)

    #First, add all the files presently in the validation folders to the list. These are the source images for the variants produced by the create_sampled_images.sh script.
    validation_filenames = os.listdir(VALIDATION_IMAGE_FOLDER)
    for filename in validation_filenames:
       #fv.write(os.getcwd() + "/" + BASE_VALIDATION_IMAGE_FOLDER +"/" + str(i) + "/"+ filename + " " + str(i) + '\n')
       fv.write(str(i) + "/"+ filename + " " + str(i) + '\n')
       
    #Then, list the files in the training folder. Let the original images be the sole validations.
    training_filenames = os.listdir(TRAINING_IMAGE_FOLDER)
    for filename in training_filenames:
      fr.write(str(i) + "/"+ filename + " " + str(i) + '\n')
  #End for-loop

  fr.close()
  fv.close()

# Standard boilerplate to call the main() function to begin the program.
if __name__ == '__main__':
	main()
