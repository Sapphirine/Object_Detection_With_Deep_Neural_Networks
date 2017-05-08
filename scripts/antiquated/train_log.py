#!/usr/bin/env python

#Adapted from http://stackoverflow.com/questions/31978186/monitor-training-validation-process-in-caffe

import pandas as pd
from matplotlib import *
from matplotlib.pyplot import *
import sys

def main():
  if (len(sys.argv)<>4):
    print("Usage: train_log.py (train file) (test file) (plot image name)")
  else:
    train=""+sys.argv[1]
    test=""+sys.argv[2]
    plotfilename=""+sys.argv[3]
    train_log = pd.read_csv(train)
    test_log = pd.read_csv(test)
    _, ax1 = subplots(figsize=(15, 10))
    ax2 = ax1.twinx()
    ax1.plot(train_log["NumIters"], train_log["loss"], alpha=0.4)
    ax1.plot(test_log["NumIters"], test_log["loss"], 'g')
    ax2.plot(test_log["NumIters"], test_log["accuracy"], 'r')
    ax1.set_xlabel('iteration')
    ax1.set_ylabel('train loss')
    ax2.set_ylabel('test accuracy')
    savefig(plotfilename)
if __name__ == '__main__':
	main()
