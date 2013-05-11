'''
获得某个目录下的所有大图并将其转移
'''
import Image
import getopt
import os
import sys
import fnmatch
import shutil

def getBigImages(dir1, dir2):
	"this is remove little pic method"
	if not os.path.exists(dir2):
		os.makedirs(dir2, 0777)
	bigPicCount = 0
	for root, dirs, files in os.walk(dir1):
		for name in files:
			if fnmatch.fnmatch(name, "*.jpg") or fnmatch.fnmatch(name, "*.jpeg"):
				cpath = os.path.join(root, name)
				img = Image.open(cpath)
				width, height = img.size
				if(width < 200) and (height < 200):
					os.remove(cpath)
				else:
					bigPicCount += 1
					os.rename(cpath, os.path.join(dir2, name))
		print dirs
		for dirp in dirs:
			dirp = os.path.join(root, dirp)
			filelist = os.listdir(dirp)
			if len(filelist) == 0 :
				os.remove(dirp)
	print "the big pic total count is ", bigPicCount

def copyBigImagesTo(dir1, dir2):
	"this is remove little pic method"
	if not os.path.exists(dir2):
		os.makedirs(dir2, 0777)
	bigPicCount = 0
	for root, dirs, files in os.walk(dir1):
		for name in files:
			if fnmatch.fnmatch(name, "*.jpg") or fnmatch.fnmatch(name, "*.jpeg"):
				cpath = os.path.join(root, name)
				img = Image.open(cpath)
				width, height = img.size
				if(width < 200) and (height < 200):
					print cpath
				else:
					bigPicCount += 1
					shutil.copyfile(cpath, os.path.join(dir2, name))
	print "the big pic total count is ", bigPicCount

def usage():
	print """Usage: python sum_primes.py [-s] <dir> [-d] <dir1>"""
	

def main(argv):
	try:
		opts, args = getopt.getopt(argv, "hs:d:", ["help", "grammar="])
	except getopt.GetoptError:
		usage()
		sys.exit(2)
	dir1 = os.getcwd()
	dir2 = os.path.join(dir1, "all")
	for opt, arg in opts:                
		if opt in ("-h", "--help"):
			usage()                     
			sys.exit()
		elif opt in ("-s", "--src"):
			if arg.startswith('./'):
				dir1 = dir1 + os.sep + arg[2:]
			else:
				dir1 = arg
		elif opt in ("-d", "--dest"):
			if arg.startswith('./'):
				dir2 = os.getcwd() + os.sep + arg[2:]
			else:
				dir2 = arg
	getBigImages(dir1, dir2)
	
if __name__ == '__main__':
	main(sys.argv[1:])
