 # coding=utf8    
import os, os.path, shutil, subprocess, stat, pickle, sys, time
from sets import Set
from string import Template 


def createDirIfNecessary(dir):
    if os.path.isdir(dir) == False:
        os.makedirs(dir)
        
def writeToExecutableScript(commands, scriptPath):
    with open(scriptPath, 'w') as file:    
        file.write("\n".join(commands))
        file.close()
        os.chmod(scriptPath, stat.S_IEXEC | stat.S_IREAD | stat.S_IWRITE)            


# move sub dirs from srcDir to destDir, only move count 
def moveSubDir(srcDir, destDir, count):
    if os.path.isdir(srcDir) == False:
        raise RuntimeError(srcDir, 'is not a dir')
        
    createDirIfNecessary(destDir)    
    dirs = sorted(os.listdir(srcDir))
    for dir in dirs:
        dirFrom = os.path.join(srcDir, dir)
        if os.path.isfile(dirFrom):
            continue
        dest = os.path.join(destDir, dir)
        if os.path.isdir(dest) == True:
            print 'can not move', dirFrom, 'to', dest
            continue
        else:
            print 'move ', dirFrom, 'to', dest
            os.rename(dirFrom, dest)
            count = count - 1
            if count <= 0:
                return        

def generateTemplate(templateFile, destFile, **kw):    
    with open(templateFile) as template:
        file = Template(template.read())
        template.close()
        with open(destFile, 'w') as target: 
            target.write(file.substitute(kw))
            target.close();

def ensureDirExists(dir):
      if os.path.isdir(dir) == False:
        raise RuntimeError(dir, 'is not a dir')

def ensureFileExists(file):
      if os.path.isfile(file) == False:
        raise RuntimeError(file, 'is not a file')

    
# index=23, maxDigitLength=9, sectionLength=3  -> 000/000/023
# index=45678, maxDigitLength=9, sectionLength=3  -> 000/045/678
def generateFileNameByIndex(index, maxDigitLength, sectionLength):
    s = str(index).zfill(maxDigitLength)
    r = ''
    while len(s) > 0 :
        r = os.path.join(s[-3:], r)
        s = s[:-3]
    return r[:-1]

def clearEmptyDirs(rootDir):
    for dirname, dirnames, filenames in os.walk(rootDir):
        if len(dirnames)==0 and len(filenames)==0:
            print 'removing empty dir', dirname
            os.removedirs(dirname)
            