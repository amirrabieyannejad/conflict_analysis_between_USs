import os
import json


directoryPath = "crf"
extractedAactions = set()

for fileName in os.listdir(directoryPath):
    if fileName.endswith(".json"):
        filePath =os.path.join(directoryPath,fileName)
        with open(filePath,'r') as inputFile:
            data = json.load(inputFile)
        for item in data:
            primaryAction = item['Action']['Primary Action']
            secondaryAction = item['Action']['Secondary Action']
            extractedAactions.add(tuple(primaryAction))
            extractedAactions.add(tuple(secondaryAction))
outputFilePath = 'unique_verbs.txt'
with open(outputFilePath,'w') as outputFile:
    for actionTuple in extractedAactions:
        outputFile.write('\n'.join(actionTuple) + '\n')

print(f"Unique verbs saved to {outputFilePath}")