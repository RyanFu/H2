#!/usr/bin/python

import glob
import os
import sys

from PIL import Image

EXTS = 'jpg', 'jpeg', 'JPG', 'JPEG', 'gif', 'GIF', 'png', 'PNG'

def avhash(im):
    if not isinstance(im, Image.Image):
        im = Image.open(im)
    im = im.resize((8, 8), Image.ANTIALIAS).convert('L')
    avg = reduce(lambda x, y: x + y, im.getdata()) / 64.
    return reduce(lambda x, (y, z): x | (z << y),
                  enumerate(map(lambda i: 0 if i < avg else 1, im.getdata())),
                  0)

def hamming(h1, h2):
    h, d = 0, h1 ^ h2
    while d:
        h += 1
        d &= d - 1
    return h

def compare(imgdest, imgDir):
    h = avhash(imgdest)
    os.chdir(imgDir)
    images = []
    for ext in EXTS:
        images.extend(glob.glob('*.%s' % ext))
    seq = []
    prog = int(len(images) > 50 and sys.stdout.isatty())
    for f in images:
        seq.append((f, hamming(avhash(f), h)))
        if prog:
            perc = 100. * prog / len(images)
            x = int(2 * perc / 5)
            print '\rCalculating... [' + '#' * x + ' ' * (40 - x) + ']',
            print '%.2f%%' % perc, '(%d/%d)' % (prog, len(images)),
            sys.stdout.flush()
            prog += 1

    if prog: print
    for f, ham in sorted(seq, key=lambda i: i[1]):
        print "%d\t%s" % (ham, f)

def similarity(imgdest, imgDir, sim, simdest):
    h = avhash(imgdest)
    currdir = os.getcwd()
    os.chdir(imgDir)
    images = []
    for ext in EXTS:
        images.extend(glob.glob('*.%s' % ext))
    simCount = 0
    prog = int(len(images) > 50 and sys.stdout.isatty())
    for f in images:
        if not os.path.isfile(fpath):
            continue
        if hamming(avhash(f), h) <= sim:
            cfpath = os.path.join(simdest, imgdest.split("/")[-1][:-4])
            if not os.path.exists(cfpath):
                os.makedirs(cfpath, 0777)
            os.rename(os.path.join(imgDir, f), os.path.join(cfpath, f))
            simCount += 1
        if prog:
            perc = 100. * prog / len(images)
            x = int(2 * perc / 5)
            sys.stdout.flush()
            prog += 1
    os.chdir(currdir)
    return simCount

if __name__ == '__main__':
    if len(sys.argv) <= 1 or len(sys.argv) > 4:
        print "Usage: %s [srcdir] [destdir] [simdir]" % sys.argv[0]
    else:
        srcdir, destdir, simdir = sys.argv[1], './dest' if len(sys.argv) < 4 else sys.argv[2], './sim' if len(sys.argv) < 4 else sys.argv[3]

        currdir = os.getcwd()
        if srcdir.lower().startswith("./"):
            srcdir = os.path.join(currdir, srcdir)        
        if destdir.lower().startswith("./"):
            destdir = os.path.join(currdir, destdir)        
        if simdir.lower().startswith("./"):
            simdir = os.path.join(currdir, simdir)
            
        if not os.path.exists(srcdir):
            print "Usage: %s [srcdir] [destdir] [simdir]" % sys.argv[0]
               
        if not os.path.exists(destdir):
            os.makedirs(destdir, 0777)
            
        os.chdir(srcdir)
        images = []
        for ext in EXTS:
            images.extend(glob.glob('*.%s' % ext))
            
        os.chdir(currdir)
        for f in images:
            fpath = os.path.join(srcdir, f)
            if not os.path.isfile(fpath):
                continue
            tpath = os.path.join(destdir, f)
            os.rename(fpath, tpath)
            if not os.path.isfile(tpath):
                continue
            if similarity(tpath, srcdir, 10, simdir) > 0:
                print f, "has similarity in ", 10, "with other files."
