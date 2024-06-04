import os
import csv
import xml.etree.ElementTree as ET

xml_directroy = "verbnet3.4"

csv_file = 'output.csv'

with open(csv_file, 'w', newline ='') as csvfile:
    csv_writer = csv.writer(csvfile)
    csv_writer.writerow(['Member_name','verb_type','Calss ID'])

    # Loop through each XML file
    for filename in os.listdir(xml_directroy):
        if filename.endswith('.xml'):
            file_path = os.path.join(xml_directroy,filename)

            # Parse the XML file
            tree = ET.parse(file_path)
            root = tree.getroot()

            # Find and extract ID, verb_type and Members von xml files
            vnclass_id = root.get('ID')
            verb_type = root.get('verb_type')
            members = root.find('.//MEMBERS')

            if members is not None:
                for member in members.findall('MEMBER'):
                    member_name = member.get('name')
                    csv_writer.writerow([member_name, verb_type, vnclass_id])

print (f'Data has been extracted and saved to {csv_file}')