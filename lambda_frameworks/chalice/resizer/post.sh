#export URL='http://localhost:8000'
export URL=`chalice url`

export PIC='floppy.jpg'

(echo -n '{"data": "'; base64 $PIC; echo '", "height": 32 , "width": 32}') |
curl -H "Content-Type: application/json" -d @-  $URL
