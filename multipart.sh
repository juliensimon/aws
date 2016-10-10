#!/bin/bash

# Author : Julien Simon <julien@julien.org>, 03/05/2016

# Disclaimer : this is for educational purposes only.
# This script was written during a flight from Paris to Stockholm
# and debugged late at night in a hotel room.
# If it destroys your infrastructure, sets your car on fire
# or breaks your marriage, it'll be your fault, not mine :)

# The file to transfer
S3_FILENAME="video.mp4"
# The S3 bucket to transfer it to
S3_BUCKET="jsimon-public"
# The size of each file part (m = Megabyte)
PART_SIZE="10m"
# The prefix for the part filename
PART_PREFIX="parts"
# How long (in seconds) you want to wait before starting the transfer of another part
PART_DELAY=1
# How long (in seconds) you want to wait before you start checking if the transfer is complete
INITIAL_DELAY=10
# How often (in seconds) you want to check if the transfer is complete
COMPLETION_DELAY=5
# How many times you want to wait for the transfer is complete
COMPLETION_TRIES=120
# The name of the JSON file holding the information on transferred parts
FILEPARTS_FILE="fileparts.json"

# BINARIES REQUIRED BY THIS SCRIPT
AWSCLI_BIN=/usr/local/bin/aws
OPENSSL_BIN=/usr/bin/openssl
SPLIT_BIN=/usr/bin/split
LS_BIN=/bin/ls
RM_BIN=/bin/rm

################# DO NOT EDIT BELOW THIS LINE

# Compute MD5 hash of file
echo "Computing MD5 hash"
md5=`$OPENSSL_BIN md5 -binary $S3_FILENAME | base64`

# Split file and count number of parts
echo "Splitting file"
$SPLIT_BIN -b$PART_SIZE $S3_FILENAME $PART_PREFIX
part_count=`$LS_BIN -l $PART_PREFIX* | wc -l`

# Create multipart upload and store upload id
echo "Starting multipart upload"
upload_id=`$AWSCLI_BIN s3api create-multipart-upload --bucket $S3_BUCKET --key $S3_FILENAME --metadata md5=$md5 --query "UploadId" --output text`

# Initialize counter for parts
count=1

# For each part:
for part in `$LS_BIN $PART_PREFIX* | xargs`
do
  # Compute MD5 hash
  md5_part=`$OPENSSL_BIN md5 -binary $part | base64`
  # Start upload as background job
  echo "Uploading part $count"
  $AWSCLI_BIN s3api upload-part --bucket $S3_BUCKET --key $S3_FILENAME --part-number $count --body $part --upload-id $upload_id --content-md5 $md5_part > /dev/null &
  # Wait for $PART_DELAY seconds (probably don't want to start 1000 TCP connections in 1 second)
  sleep $PART_DELAY
  ((count=count+1))
done

# Wait a little
sleep $INITIAL_DELAY
# Initialize counter for completion tries
count=0

while (true)
do
    # Every $COMPLETION_DELAY seconds, check if transfer is complete (number of transferred parts = number of parts)
    # If it is, complete the transfer using $FILEPARTS_FILE and display the object metadata
    sleep $COMPLETION_DELAY
    part_count_transferred=`$AWSCLI_BIN s3api list-parts --bucket $S3_BUCKET --key $S3_FILENAME --upload-id $upload_id --query "length(Parts)"`
    echo "$part_count_transferred parts complete"
    if [ $part_count_transferred = $part_count ]
    then
      echo "Transfer is successful"
      echo "{\"Parts\":" > $FILEPARTS_FILE
      $AWSCLI_BIN s3api list-parts --bucket $S3_BUCKET --key $S3_FILENAME --upload-id $upload_id --query "Parts[*].{ETag:ETag, PartNumber:PartNumber}" >> $FILEPARTS_FILE
      echo "}" >> $FILEPARTS_FILE
      $AWSCLI_BIN s3api complete-multipart-upload --bucket $S3_BUCKET --key $S3_FILENAME --upload-id $upload_id --multipart-upload file://$FILEPARTS_FILE
      # Display the object metadata
      $AWSCLI_BIN s3api head-object --bucket $S3_BUCKET --key $S3_FILENAME
      break
    fi

    ((count=count+1))
    # If transfer is incomplete and $COMPLETION_TRIES is reached, abort the transfer
    if [ $count = $COMPLETION_TRIES ]
    then
      echo "Transfer is unsuccessful"
      $AWSCLI_BIN s3api abort-multipart-upload --bucket $S3_BUCKET --key $S3_FILENAME --upload-id $upload_id
    fi
done

# Clean up
$RM_BIN -f $FILEPARTS_FILE $PART_PREFIX*
