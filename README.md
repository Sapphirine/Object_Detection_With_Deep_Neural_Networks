# Object Detection with Deep Neural Networks, EECS 6895, Columbia University, Spring 2017

## Requirements

### For Visual Search Engine (Course Project)

* Android 4.0+ support (for Visual Search Engine Project)

* ARMv7 or x86 based device (the OpenCV libraries that do the object detection are built for these CPU types)

* Build with Gradle. You can use Android studio to build as well.

* To learn more about this project, please see README.md in the Visual_Search_Engine folder.

### For Fast RCNN (Used to build object detection models)

* [CAFFE] (https://github.com/BVLC/caffe)

* [FastRCNN](https://github.com/rbgirshick/caffe-fast-rcnn). Ensure that you have all the software and hardware requirements necessary for building models.

* For this course, CaffeNet models were used. If wanting to replicate my results (or at least follow my path), copy the CaffeNet_EECS6895 folder to your models folder after setting up Fast RCNN.

## Development advice for Fast RCNN

Building Fast RCNN can be a difficult task. Here are some general instructions to get you past some of the more difficult parts. These instructions apply to a system running Ubuntu 16.04 Linux.

* Build OpenCV and CAFFE with Python bindings according to [this ](https://github.com/BVLC/caffe/wiki/OpenCV-3.1-Installation-Guide-on-Ubuntu-16.04). If you have an Nvidia GPU, I strongly recommend you install CUDA to enable GPU processing. First step: download required source for both.

* Next step is to build OpenCV. Follow the instructions on what CMAKE parameters to build OpenCV with. I used cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -D WITH_TBB=ON -D WITH_V4L=ON -D WITH_QT=ON -D WITH_OPENGL=ON -D WITH_CUBLAS=ON -DCUDA_NVCC_FLAGS="-D_FORCE_INLINES" -D INSTALL_PYTHON_EXAMPLES=ON .. . This sets OpenCV to be built with the following: TBB (Intel Threading Blocks), V4L, QT, CUBLAS, CUDA. It also turns on the Python examples. Note: QT is optional. DO NOT enable the extra OpenCV modules (-DOPENCV_EXTRA_MODULES_PATH=..) or OpenCV's Deep Neural Network module-a dependency issue has been known to arise when attempting to build Python enabled CAFFE (there is some tangle involving libopencv_dnn_modern.so). To be double sure of not building OpenCV's DNN, include -D BUILD_OPENCV_DNN=OFF in your cmake statement.

* Once CAFFE has been built, GIT clone Fast [FastRCNN](https://github.com/rbgirshick/caffe-fast-rcnn).

* Once Fast RCNN has been cloned, go into the repository's caffe-fast-rcnn folder. Copy the Makefile.config used to build CAFFE into this folder, which should be a specialized fork of the master CAFFE branch. Mind-since Fast RCNN is no longer supported, if there any changes to the master branch, this Makefile.config could no longer be useful. Fast RCNN has been replaced by Faster RCNN and is only used in this project because of the Visual Search Engine's use of Fast RCNN models (in turn, Visual Search Engine may be eventually updated to use Faster RCNN).

* Once copied, you should be able to run Make and build the binaries and such. Consult with the build notes for what to do next.

* Fast RCNN requires Protobuf 2.6. It is recommended that this program be built from source, as the versions in some distributions are not the same version (See [here](https://github.com/rbgirshick/py-faster-rcnn/issues/24) for more information).


## Advice on how to train your own model

Training your own model can be difficult. If you are going to do it with Fast RCNN, follow roughly this tutorial [here](http://sgsai.blogspot.com/2016/02/training-faster-r-cnn-on-custom-dataset.html). Rather than trying to re-create the entire path specified, use instead my SGS folder to establish the format. For this course, I was trying to train my own model to recognize three objects-you can expand and change the number of objects you are trying to detect.

Here are some things you may want to look at from my notes on this matter.

* Folder arrangement (see SGS folder):
Project name (here, SGS)
|-- data
    |-- Annotations
         |-- *.txt (Annotation files)
    |-- Images
         |-- *.png (Image files)
    |-- ImageSets
         |-- train.txt (needed for training)
         |-- test.txt (needed if testing)

* Annotations folder: each file .txt has to be in the same format and should be named for the file they correspond to. For example, cropped_000001.txt is the annotation file for the image cropped_000001.

* Annotation file format: (Object/class number) (top left coordinate of bounding box indicating where object is in image) (bottom right coordinate of bounding box indicating where object is in image). For example, "1 200 200 314 300" corresponds to class number 1, where object appears in bounding box defined by (200, 200) and (314, 300). NOTE: Class number is zero based, and zero is reserved for background.

* Images is where all training and testing images are located.

* ImageSet files are simply lists of files used in both training and testing. You can modify and use the script create_label_file.py in the scripts folder to generate these. Note: Do not include the filetype prefix for these lists.

* Fast RCNN has a dependency on Matlab to create the train.mat and test.mat files needed for training and testing. If this is a problem, move up to Faster RCNN, which is Matlab free and more current.

## Errata

* There are several scripts you can use in the scripts folder. Some are antiquated. The add_noise.sh script uses ImageMagick to create a copy of an image and add noise to it; this was done to create testing images for my research. The approach did not work unfortunately-please read my research paper to see why. Maybe you can make it work? If you do, please let me know! This research was my first foray into machine learning and object detection, and I am eager to clear up any mistakes and build something even better.

## License

	    Copyright (C) 2017 Ihimu Ukpo
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
