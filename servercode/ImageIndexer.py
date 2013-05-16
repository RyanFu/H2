import os

if __name__ == '__main__':
	cpath = '.'
	for pdir, dirs, files in os.walk(cpath):
		index = 0
		for file in files:
			if file[-4:] == ".jpg":
				os.rename(file, "i_{0}.jpg".format(index))
				index += 1

	for pdir, dirs, files in os.walk(cpath):
		index = 0
		for file in files:
			if file[-4:] == ".jpg":
				os.rename(file, "{0}.jpg".format(index))
				index += 1
		f = open('n', 'w')
		f.write("{0}".format(index))
		f.close()