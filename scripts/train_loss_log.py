#!/usr/bin/env python

#Produces a graph of iteration number versus loss.

#Adapted from https://haoyu.love/blog392.html

'''
Log should be purely of the form below:
I0507 15:22:16.305663  8507 solver.cpp:189] Iteration 0, loss = 3.12608
I0507 15:22:16.305696  8507 solver.cpp:204]     Train net output #0: loss_bbox = 0.195958 (* 1 = 0.195958 loss)
I0507 15:22:16.305701  8507 solver.cpp:204]     Train net output #1: loss_cls = 2.93012 (* 1 = 2.93012 loss)
'''



import re
import numpy as np
import matplotlib.pyplot as plt
import sys
 
def str2num(str_list, output_type):
    return [output_type(x) for x in str_list]
 
 
if "__main__" == __name__:

    if (len(sys.argv)<>2):
      print("Usage: train_log_graph_script.py (train file)")
    else:
      log_file = ""+sys.argv[1]
      pattern_itr = re.compile(r"Iteration\s+([\d]+)")
      pattern_rpn = re.compile(r"loss_cls[\s=]{1,3}([\d\.]+)")
      pattern_box = re.compile(r"loss_bbox[\s=]{1,3}([\d\.]+)")
 
      with open(log_file, 'r') as f:
        lines = f.read()
        itrs = pattern_itr.findall(lines)
        rpns = pattern_rpn.findall(lines)
        boxs = pattern_box.findall(lines)

	#Getting duplicates because of two entries with line "Iteration: ". Same values. So, just return the unique set. 
        itrs = np.unique(str2num(itrs, int))
        rpns = np.array(str2num(rpns, float))
        boxs = np.array(str2num(boxs, float))
 
        plt.figure()
        plt.sca(plt.subplot(211))
        plt.plot(itrs, rpns)
        plt.title("Class Loss Per Iteration")
	plt.xlabel("Number of Iterations")
	plt.ylabel("Loss")
 
        plt.sca(plt.subplot(212))
        plt.plot(itrs, boxs)
        plt.title("Boundary Box Loss")
	plt.xlabel("Number of Iterations")
	plt.ylabel("Loss")
 
        plt.show()
