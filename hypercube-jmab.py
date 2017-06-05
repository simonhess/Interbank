import os
import pandas as pd
import datetime
import shutil

xls = pd.read_excel('NOLHdesigns_v6.xls',sheetname='NOLH for up to 11 factors', header=4, parse_cols='B:AA')
experiments = len(xls.index)
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
# for every experiment create a folder and add a property file to it
for experiment in range(experiments):
    foldername= 'experiment{}'.format(str(experiment+1))
    os.makedirs(foldername)
    file = open('{}/experiment.properties'.format(foldername),'w') 
    file.write('#{}\n'.format(str(datetime.datetime.now()))) 
    file.write('fileNamePrefix.value={}/{}/\n'.format(dataFolder, foldername)) 
    # take columnname 
    for colName in xls.columns.values:
        celValue = str(xls[colName][experiment])
        file.write('{}={}\n'.format(colName, celValue))
    file.close() 