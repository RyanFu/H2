# coding=utf8    
import os, os.path, shutil, subprocess, stat, pickle, sys, time, commonlib
from sets import Set
from PIL import Image
from string import Template
 

## command options
# mvdirs [src_dir] [dest_dir] [count] - move count sub dir from src dir to dest dir
# webprepare [dir] - generate script for every sub dir in dir
# merge [src_dir] [dest_dir] [digit_count] - move all sub dir from src_dir to dest_dir, naming them accordingly with digit_count digits, and starting from the largest folder's number
# updateindex [index_path] [img_dir] - update index html     
# updatetotaljs [index_path] [img_dir] - update totaljs file     
# thumb [img_dir] [script_name]  - generate thumbnail for all images in sub dirs
# plainmerge [src] [dest] [digit_count] - do no fancy stuff, simply copy sub dir from src to dest, rename it accordingly
# mergehuaban [src] [dest] [digit_count] 
# makealbum [src_dir] [dest_dir] [album_count] [image_count_per_album] 
#   - src_dir contains standalone images, make album_count number of albums, 
#     with each album contain image_count_per_album number of albums
# removefile [src_dir] [suffix_to_remove] remove files that does not have [suffix_to_remove]
# normalizefile [src_dir] rename and make sure the file are named sequentially, in lower case
# moveimage [src_dir] [dest_dir] [dest_image_digit_length]  [number_of_images] [www_image_count_file_path]
#    - src_dir is any folder with images in any
#    - dest_dir is a dir with digitalized image in it, flat dir structure
#    - dest_image_digit_length is how many digit each of these images contain
#    - number_of_images how many images to move
#    - www_image_count_file_path path to update for total image count
# numberimage [src_dir] [dest_dir]  
#    order image by numbers, if there is any sub directory in dest dir, will move image out
#      
# shrinkimage [src_dir]
# geninstall [src_dir]
#    generate install command for each file in src_dir    



def entry():
    args = sys.argv
    if len(args) == 1:
        print 'Not enough arguments'
        return
    
    scriptDir = os.path.dirname(args[0])
    a1 = args[1]
    
    if a1 == 'mvdirs':        
        commonlib.moveSubDir(args[2], args[3], int(args[4]))
    elif a1 == 'webprepare':
        webPrepare(args[2], os.path.join(scriptDir, 'templates', 'template001.js'))
    elif a1 == 'merge':
        mergeFolder(args[2], args[3], int(args[4]))        
    elif a1 == 'updateindex':
        updateIndex(args[2], args[3], os.path.join(scriptDir, 'templates', 'index.htm'))
    elif a1 == 'updatetotaljs':
        updateTotalJs(args[2], args[3], os.path.join(scriptDir, 'templates', 'template002.js'))
    elif a1 == 'thumb':
        generateThumbnail(args[2], args[3])
    elif a1 == 'plainmerge':
        plainMerge(args[2], args[3], int(args[4]))
    elif a1 == 'mergehuaban':
        mergeHuaban(args[2], args[3], int(args[4]))
    elif a1 == 'copysingle':
        makeAlbums(args[2], args[3], int(args[4]), int(args[5]))
    elif a1 == 'removefile':
        removeFileWithoutSuffix(args[2], args[3])
    elif a1 == 'normalizefile':
        normalizeFile(args[2])     
    elif a1 == 'moveimage':
        moveHuabanImage(args[2], args[3], int(args[4]), int(args[5]), args[6])
    elif a1 == 'numberimage':
        numberImage(args[2], args[3])
    elif a1 == 'shrinkimage':
        shrinkImage(args[2])
    elif a1 == 'geninstall':
        genInstall(args[2])
    elif a1 == 'test':
        test()
    else:
        print 'unrecognized command name'        


def test():
    print getLargestFileNumberNew('/home/yang/kktest1241')
    
def genInstall(srcDir):
    r = []
    r1 = []
    for dirname, dirnames, filenames in os.walk(srcDir):
        filenames = sorted([f for f in filenames if f.lower().endswith('.apk')])
        a = ['adb install ' + os.path.join(dirname, f) for f in filenames]
        b = ['adb uninstall ' + f[:-10] for f in filenames]
        r.extend(a)        
        r1.extend(b);
    for s in r:
        print s
    print '-------------------------------------------------'
    for s in r1:
        print s        
        
def shrinkImage(srcDir):
    maxSize = 1024 * 300;
    maxWidth = 640;
    for dirname, dirnames, filenames in os.walk(srcDir):
        filenames = sorted([f for f in filenames if f.lower().endswith('.jpg')])
        for file in filenames:
            file = os.path.join(dirname, file)
            if os.path.getsize(file) < maxSize:
                continue
                        
            image = Image.open(file)
            w, h = image.size[0], image.size[1]
            if w > maxWidth:
                print 'shrink ', file
                w = maxWidth
                h = 1000                
                image.thumbnail((w, h))
                image.save(file, "JPEG")            
                        
def numberImage(srcDir, destDir):
    commonlib.createDirIfNecessary(destDir)
    destFiles = os.listdir(destDir)
    for file in destFiles:        
        if file != 'icon.jpg' and os.path.isfile(os.path.join(destDir, file)) :
            os.remove(os.path.join(destDir, file))
                
    count = 1
    for dirname, dirnames, filenames in os.walk(srcDir):
        filenames = sorted([f for f in filenames if f.lower().endswith('.jpg')])        
        for file in filenames:                            
            srcFile = os.path.join(dirname, file)
            if file == 'icon.jpg':
                targetFile = os.path.join(destDir, file)
            else:
                targetFile = os.path.join(destDir, str(count) + '.jpg')
            if srcFile != targetFile:
                print 'rename', srcFile, 'to', targetFile
                os.rename(srcFile, targetFile)
            else:
                print 'skip', srcFile
            count = count + 1
                                
            
def getLargestFileNumberNew(rootDir):     
    if os.path.isdir(rootDir) == False:
         raise RuntimeError('Not a dir ', rootDir)
    dirs = [dir for dir in  os.listdir(rootDir) if dir.split('.')[0].isdigit() == True]
    
    if dirs == None or len(dirs) == 0:        
        return 0    
    dirs.sort()
    dirs.reverse()    
    
    file = os.path.join(rootDir, dirs[0])
    if os.path.isfile(file):
        # file is something like /tmp/img/000/010/001.jpg, I only want to get the 000/010/001 part
        file = file.split('.')[0]
        file = file.split('/')[-3:]
        file = ''.join(file)        
        return int(file)
    elif os.path.isdir(file):
        return getLargestFileNumberNew(file)
    else:
        raise RuntimeError('Not a dir or file ', file)            

def moveHuabanImage(srcDir, destDir, destNameLength, n, imageCountPath):
    print ''
    print '######################### moving huaban images ###############################'
    moveImageToDir_Huaban(srcDir, destDir, destNameLength, n)
    print ''
    print '######################### updating image count ###############################'
    updateTotalImageCountFile(destDir, imageCountPath)
    print ''
    print '######################### clearing empty dirs ###############################'    
    commonlib.clearEmptyDirs(srcDir)
    pass

def updateTotalImageCountFile(imageDir, path):
    counter = getLargestFileNumberNew(imageDir)
    with open(path, 'w') as f:
        f.write(str(counter))
        f.close()
    print 'update total image count to', counter

def moveImageToDir_Huaban(srcDir, destDir, destNameLength, n):        
    # 1. get the largest file number in destDir
    counter = getLargestFileNumberNew(destDir)
    print 'have ', counter, 'images'
    counter = counter + 1
    
    # 2. walk srcDir for n times     
    for dirname, dirnames, filenames in os.walk(srcDir):
        if len(dirnames) == 0 and len(filenames) == 0:
            continue
        
        for file in [os.path.join(dirname, file) for file in sorted(filenames)]:
            print 'processing image ' + file
            destFile = os.path.join(destDir, commonlib.generateFileNameByIndex(counter, 9, 3))
            destThumbnail = destFile + 's.jpg'
            destFile = destFile + '.jpg'
            
            # create temp dir if necessary
            commonlib.createDirIfNecessary(os.path.dirname(destFile))
            
            # generate thumbnail    
            print 'thumbnail', destThumbnail
            try:
                imageThumbnail(file, destThumbnail)
            except IOError as e:
                print 'failed process', file
                print e
                os.remove(file)
                continue
                        
            # move file to 
            print 'moveto', destFile            
            os.rename(file, destFile)
            
            counter = counter + 1 
            n = n - 1
            if n <= 0:
                return
            
def imageThumbnail(src, dest):
    maxWidth = 320        
    image = Image.open(src)
    w, h = image.size[0], image.size[1]
    if w <= maxWidth:
        pass    
    else:
        w = 320
        h = 1000
        
    image.thumbnail((w, h))
    image.save(dest, "JPEG")
    
def normalizeFile(rootDir):
    for dir in os.listdir(rootDir):
        dir = os.path.join(rootDir, dir)
        if os.path.isdir(dir) == False:
            print 'skipping', dir, ' not a dir'
            continue
        
        counter = 1
        for file in sorted(os.listdir(dir)):
            src = os.path.join(dir, file)            
            dest = os.path.join(dir, str(counter) + '.jpg')
            if os.path.isfile(src) == False:
                print 'skipping', src, ', not a file'
                continue            
            if src == dest :
                continue
            if os.path.isfile(dest) == True:
                print 'skipping', src, ', dest already exist', dest
                counter = counter + 1
                continue            
            print 'rename', src, 'to', dest
            os.rename(src, dest)
            counter = counter + 1    
    
    
def removeFileWithoutSuffix(srcDir, suffix):
    suffix = suffix.lower()
    for dirname, dirnames, filenames in os.walk(srcDir):
        for filename in filenames:
            if filename.lower().endswith(suffix) == False:            
                file = os.path.join(dirname, filename)            
                print 'remove', file
                os.remove(file)            
    
def makeAlbums(srcDir, destDir, albumCount, imageCountPerAlbum):
    commonlib.ensureDirExists(srcDir)
    commonlib.createDirIfNecessary(destDir)
    for i in range(albumCount):
        albumDir = os.path.join(destDir, str(i))
        print 'creating album at', albumDir, 'copy', imageCountPerAlbum, 'images from', srcDir
        _makeAlbum(srcDir, albumDir, imageCountPerAlbum)
        
def _makeAlbum(srcDir, albumDir, imageCount):
    commonlib.createDirIfNecessary(albumDir)
    srcFiles = sorted(os.listdir(srcDir))
    totalLen = len(srcFiles)    
    for i in range(imageCount):
        if i < totalLen:
            srcFile = os.path.join(srcDir, srcFiles[i])
            destFile = os.path.join(albumDir, str(i + 1) + '.jpg')
            print 'move', srcFile , 'to' , destFile
            os.rename(srcFile, destFile)    
    
def mergeHuaban(srcDir, destDir, nameLength):
    commonlib.ensureDirExists(srcDir)
    commonlib.createDirIfNecessary(destDir)
    counter = 1    
    for dirname, dirnames, filenames in os.walk(srcDir):
        for filename in filenames:            
            srcFile = os.path.join(dirname, filename)            
            destFile = os.path.join(destDir, str(counter).zfill(nameLength) + '_' + filename.split('_')[0] + '.jpg')
            print 'rename', srcFile, 'to', destFile
            os.rename(srcFile, destFile)
            counter = counter + 1
            
def plainMerge(srcDir, destDir, nameLength):
    commonlib.ensureDirExists(srcDir)
    commonlib.createDirIfNecessary(destDir)
    
    destDirs = sorted(os.listdir(destDir))
    counter = 0
    
    if len(destDirs) != 0:
        counter = int(destDirs[-1])
    
    srcDirs = [os.path.join(srcDir, dir) for dir in sorted(os.listdir(srcDir))]
    
    for dir in srcDirs:
        counter = counter + 1
        toDir = os.path.join(destDir, str(counter).zfill(nameLength))
        print 'move', dir, 'to', toDir
        os.rename(dir, toDir)
    
#convert -resize 320x\> -quality 30 /home/yang/Desktop/201207_original/009/2.jpg /tmp/30.jpg
resizeCommand = 'convert -resize 320x\> -quality 45 {} {}'

# for images in every dir of rootDir, generate thumbnail for it
def generateThumbnail(rootDir, scriptFile):
    commonlib.ensureDirExists(rootDir)
        
    commands = ['#!/bin/bash -v']
    dirs = sorted(os.listdir(rootDir))        
    dirs = [os.path.join(rootDir, dir) for dir in dirs]    
    if len(dirs) == 0:
        print 'no file to process in dir', rootDir
        return
    
    for dir in dirs:                       
        ta = [(os.path.join(dir, file), os.path.join(dir, 't_' + file)) for file in sorted(os.listdir(dir))]        
        commands.extend([resizeCommand.format(originalFile, newFile) for originalFile, newFile in ta])        
    commonlib.writeToExecutableScript(commands, scriptFile)        

def updateIndex(indexFile, imgDir, indexTemplate):
    commonlib.generateTemplate(indexTemplate, indexFile, totalPage=str(int(getLargestFileName(imgDir))))    

def updateTotalJs(jsFile, imgDir, jsTemplate):
    commonlib.generateTemplate(jsTemplate, jsFile, total=str(int(getLargestFileName(imgDir))))    

def webPrepare(rootDir, jsTemplate):
    commonlib.ensureDirExists(rootDir)
    commonlib.ensureFileExists(jsTemplate)
    
    dirs = os.listdir(rootDir)
    for dir in dirs:
        dir = os.path.join(rootDir, dir)
        if os.path.isdir(dir) == False:
            raise RuntimeError('not expecting a file ', dir)
        fileCount = len(os.listdir(dir)) / 2        
        sizes = [Image.open(os.path.join(dir , 't_' + str(i + 1) + '.jpg')).size for i in range(fileCount)]
        sizes = ["{}x{}".format(str(width), str(height)) for width, height in sizes]
        commonlib.generateTemplate(jsTemplate, os.path.join(dir, 'a.js'), total=str(fileCount), sizes=sizes)    
    
# move all folders in srcFolder to destFolder, naming the original folders in srcFolder accordingly to nameLength    
def mergeFolder(srcDir, destDir, nameLength):
    commonlib.ensureDirExists(srcDir)

    counter = int(getLargestFileName(destDir)) + 1    
    srcDirs = sorted(os.listdir(srcDir))
        
    for dir in srcDirs:        
        tDir = getFullDirName(destDir, counter, nameLength)
        sDir = os.path.join(srcDir, dir)
        commonlib.createDirIfNecessary(os.path.dirname(tDir))
        print 'Move', sDir, 'to', tDir
        os.rename(sDir, tDir)        
        counter = counter + 1     

def getLargestFileName(rootDir):    
    if os.path.isdir(rootDir) == False:
        return '0'
    
    dirs = os.listdir(rootDir)
    for d in dirs:
        if os.path.isdir(os.path.join(rootDir, d)) == True and d.isdigit() == False:            
            raise RuntimeError('Have none digit dir', os.path.join(rootDir, d))
        
    dirs.sort()
    dirs.reverse()    
    if dirs == None or len(dirs) == 0:
        return '0'
    else:
        for dir in dirs:
            if os.path.isdir(os.path.join(rootDir, dir)):
                return getLargestFileName(os.path.join(rootDir, dir))
        # root dir contains only file, is the dir we are looing for
        return rootDir.split('/')[-1]

               
def getFullDirName(root, index, nameLength):
    fullDirName = str(index).zfill(nameLength)
    dir1 = str(int(index / 1000)).zfill(3)
    dir2 = str((int(index / 1000) / 1000)).zfill(3)
    return os.path.join(root, dir2, dir1, fullDirName)    

def getFullDirName2(root, index, nameLength):
    if nameLength < 9:
        raise RuntimeError('name length can not be less than 9', nameLength)
    fullDirName = str(index).zfill(nameLength)
    dir1 = fullDirName[-6:-3]
    dir2 = fullDirName[-9:-6]
    return os.path.join(root, dir2, dir1, fullDirName)        


#print getLargestFileName('/var/www/ax/imgs')    
entry()    
