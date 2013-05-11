# coding=utf8    
import os, os.path, shutil, subprocess, stat, pickle, sys, time, commonlib
from sets import Set
from PIL import Image
from string import Template

def entry():
    args = sys.argv
    if len(args) == 1:
        print 'Not enough arguments'
        return
    
    scriptDir = os.path.dirname(args[0])
    a1 = args[1]
    
    if a1 == '101nights':        
        m101nights(args[2], args[3])       
    elif a1 == 'test':
        test()
    else:
        print 'unrecognized command name'        

def getNumber(s):
    for i in range(len(s)):
        if s[i].isdigit() == False:            
            return int(s[0:i])
    return s

def m101nights(src, dest):    
    files = os.listdir(src)
    files = [f for f in files if f.endswith('.txt')]
    files = sorted(files, key=getNumber)        
    commonlib.createDirIfNecessary(dest)
    counter = 0
    infos = []        
    for file in files:
        counter = counter + 1
        index = file.find('\xc2')
        if index != -1:
            index = index + 2
        else:
            index = file.find(' ')
            index = index + 1
            
        endIndex = file.index('.txt')
        
        bookName = file[index:endIndex]
        oldFile = os.path.join(src, file)
        newFile = os.path.join(dest, str(counter).zfill(3) + '.txt')
        infos.append(str(counter).zfill(3) + '@@@' + str(counter) + '. ' + bookName)        
        print oldFile, newFile
        appendNewLineCopy(oldFile, newFile)

    f = open(os.path.join(dest, 'info'), 'w')
    f.write('\n'.join(infos))
    f.close()
#        os.rename(os.path.join(src, file), os.path.join(dest, file))
        

def appendNewLineCopy(src, dest):
    fsrc = open(src, 'r')
    fdest = open(dest, 'w')
    lines = [('    ' + f.strip()) for f in fsrc.readlines()]        
    fdest.write('\n\n'.join(lines))
    fsrc.close()
    fdest.close()
    
def test():
    print getLargestFileNumberNew('/home/yang/kktest1241')
    
    
entry()    
    
