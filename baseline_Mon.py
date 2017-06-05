import os
import pandas as pd
import datetime
import shutil

parent_dir = os.getcwd()
# if there is a data folder, delete it and its content
try: 
    shutil.rmtree('data')
except:
    pass
# make a data folder and go into it
dataFolder = 'data'
os.makedirs(dataFolder)
os.chdir(dataFolder)
# add property file
foldername = 'experiment1'
os.makedirs(foldername)
file = open('{}/experiment.properties'.format(foldername),'w') 
file.write('#{}\n'.format(str(datetime.datetime.now()))) 
file.write('fileNamePrefix.value={}/{}/\n'.format(dataFolder, foldername)) 
file.close()