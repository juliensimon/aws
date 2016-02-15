#!/bin/bash

# Configuration files for groups and events (1 meetup.com URL per line)
MEETUP_GROUPS=meetup_groups.txt
MEETUP_EVENTS=meetup_events.txt

# External programs
RM="/bin/rm -f"
DATE="/bin/date +"%d/%m/%y""
CUT="/usr/bin/cut"
SED="/usr/bin/sed"
GREP="/usr/bin/grep"
WGET="/usr/local/bin/wget --quiet"
PUP="/usr/local/bin/pup"

# Check existence of configuration files
if [ ! -f $MEETUP_GROUPS ] || [ ! -f $MEETUP_EVENTS ]
then
	echo "Configuration file is missing"
	exit
fi

current_date=`$DATE`

#
# Scrape event pages
#

echo "\nEVENTS\n"

while read line 
do
    if [ -n $line ]
    then
      filename=/tmp/index.$RANDOM.html
      $WGET -O $filename $line
      event_date=`cat $filename | $PUP 'h3:contains("2016") text{}'`
      number=`cat $filename | $PUP 'span[class="rsvp-count-number rsvp-count-going"] text{}'`
      $RM $filename
      name=`echo "$line" | $CUT -d '/' -f 5`
      echo $current_date";"$event_date";"$name";"$number";"
    fi 
done < $MEETUP_EVENTS 

#
# Scrape group pages
#

echo "\nGROUPS\n"

while read line 
do
    if [ -n $line ]
    then
      filename=/tmp/index.$RANDOM.html
      $WGET -O $filename $line
      number=`cat $filename | $PUP 'span[class="lastUnit align-right"]' | $PUP 'span:first-of-type text{}' | $GREP -v -e '^$'`
      $RM $filename
      name=`echo "$line" | $CUT -d '/' -f 5`
      number=$(echo $number | $SED -e 's/^ *//g;s/ *$//g') # Trim whitespaces
      echo $current_date";"$name";"$number";"
    fi 
done < $MEETUP_GROUPS

