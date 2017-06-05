python hypercube-jmab.py
FOR /r %%i IN (*.properties) DO java -jar -Djabm.config=model/mainBaselineMonteCarlo_Mon.xml -Djabm.propertyfile=%%i -Xmx4G -Xms4G interbank.jar
pause