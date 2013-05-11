# coding=utf8    
import os, os.path, shutil, subprocess, stat, pickle, sys, time, commonlib
from sets import Set
from PIL import Image
from string import Template


ImageTargetDir = '/tmp/imgtest'
ImageSrcDir = '/home/yang/Desktop/tmp'
counter = 0

for dirname, dirnames, filenames in os.walk(ImageSrcDir):
    for file in [os.path.join(dirname, file) for file in filenames]:
        src = file
        target = os.path.join(ImageTargetDir, generateFileNameByIndex(counter, 9, 3))                    
        targetDir = os.path.dirname(target)
        print 'copying ', src, 'to ' , target
        commonlib.createDirIfNecessary(targetDir)        
        shutil.copy(src, target + 's.jpg')
        os.rename(src, target + '.jpg')                        
        counter = counter + 1
