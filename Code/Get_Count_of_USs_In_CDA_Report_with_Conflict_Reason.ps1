# Define the directory you want to iterate through
$backlog = "g22"
$directoryPath = "C:\Users\amirr\eclipse-workspace_2023_12\CDA_Reports\CDA_Report_backlog_"+ $backlog
$outputfilepath= "C:\Users\amirr\eclipse-workspace_2023_12\CDA_Reports\"+ $backlog +".txt"
$jsonFilePath= "C:\Users\amirr\eclipse-workspace_2023_12\CDA_Reports\"+ $backlog + ".json"
$count = 0
$data = @()

# Example: Add an entry for the main directory and its subdirectory count
$mainSubdirCount = (Get-ChildItem -Path $directoryPath -Directory).Count
$data += @{
    
    Project_Nr = $backlog
    US_Pairs_Count = $mainSubdirCount
    _Comment = "SEE END OF THE FILE FOR SEEING AVERAGE AND TOTAL NUMBER OF CONFLICT REASON IN DATASET"
}

# Example: Add entries for each subdirectory and its subdirectory count
$subdirectories = Get-ChildItem -Path $directoryPath -Directory
foreach ($subdir in $subdirectories) {
    $subdirCount = (Get-ChildItem -Path $subdir.FullName -Directory).Count
    $count+= $subdirCount
    if($subdirCount -gt 1){
    $data += @{
        US_Pair = $subdir.Name
        Conflict_Reason_Count = $subdirCount
    }
    }
}
$data += @{
    Total_Conflict_Reasons = $count
    Average_of_Conflict_Reason_per_Conflict_pair = $count / $mainSubdirCount
    
}

# Convert the array of objects to JSON format
$json = $data | ConvertTo-Json -Depth 3

# Save the JSON to a file
$json | Out-File -FilePath $jsonFilePath -Encoding UTF8