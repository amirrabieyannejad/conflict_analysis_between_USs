import os
import csv
import xml.etree.cElementTree as ET
import re


# Define the directory containing the XML files
xml_directory = "verbnet3.4"

# Read the CSV file
csv_file = "verbtype.csv"

missing_type_file = "missing_types.txt"

reg_pattern= r"^\w*"

# Create a dictionary to sotre the verb types
verb_type_dict = {}

with open(csv_file, 'r') as csvfile:
    csvreader = csv.DictReader(csvfile,delimiter=';')
    for row in csvreader:
        verb_class = row["Verb Class"]
        verb_type = row["Verb Type"]
        verb_type_dict[verb_class] = verb_type

# Iterate thriugh XML files and update them
for filename in os.listdir(xml_directory):
    if filename.endswith(".xml"):
        xml_path = os.path.join(xml_directory, filename)

        # Parse the XML file
        tree = ET.parse(xml_path)
        vnclass = tree.getroot()

        # Find the VNCLASS tag and add the verb_tyep attribute
        #vnclass = root.find(".//VNCLASS")
        if vnclass is not None:
            verb_id = vnclass.attrib["ID"]
            match = re.match(reg_pattern,verb_id)
            if match:
                verb_class = match.group(0)
                print("Extracte Text:", match.group(0))
            else:
                print("No match found.")
            if verb_class in verb_type_dict:
                verb_type = verb_type_dict[verb_class]
                vnclass.set("verb_type", verb_type)

                # Save the updated XML file
                tree.write(xml_path)
            else:
                # if verb class not found, write it to the missing_types.txt
                with open(missing_type_file,"a") as missing_file:
                    missing_file.write(verb_class + "\n")

        print(f"Updated {filename}.")



