import os, os.path, shutil, subprocess, stat, pickle, sys, time
from sets import Set
from PIL import Image
from string import Template

dir='/y/av1/';

def listonlyjpg(dir):
    for f in os.listdir(dir):
        path = os.path.join(dirE,f)
        if os.path.isdir(path) == False:
            continue
        for x in os.listdir(path):
            if x.endswith('.jpg')==False:
                print x



def write_n(dir):
    for f in os.listdir(dir):
        path = os.path.join(dir,f)
        if os.path.isdir(path):
            size = len(os.listdir(path))
            with open(os.path.join(path, 'n'),'w') as file:
                file.write(str(size))
                file.close()
                print os.path.join(path, 'n')
    #            break



def rename():
    with open('/y/git/H2/H2/client/girls3/assets/langs/zh-CN/info') as file:
        for l in file:
            r = l.split('@@@')
            print 'mv *' + r[1] + '* ' + dir + r[0].split('.')[0]


        