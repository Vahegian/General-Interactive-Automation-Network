import os

'''
05.Jan.2019     Vahe Grigoryan

The script renames files from '0-inf' 
from folder supplied by user.
It stores the renamed files in a directory 'RenamedImages' 
'''

imdir = 'RenamedImages'
if not os.path.isdir(imdir):
    os.mkdir(imdir)

# folders = [folder for folder in os.listdir('.') if 'fidget' in folder]

n = 0
# for folder in folders:
folder = os.scandir(input('img dir > '))
for file in folder:
    os.rename(file.path, os.path.join(imdir, '{:06}.jpg'.format(n)))
    n += 1