#export URL='http://localhost:8000'
export URL='https://19ugmpi9ta.execute-api.us-east-1.amazonaws.com/api/'

export PIC='floppy.jpg'

(echo -n '{"data": "'; base64 $PIC; echo '", "topk": 1}') |
curl -H "Content-Type: application/json" -d @-  $URL

echo

(echo -n '{"data": "'; base64 $PIC; echo '", "topk": 3}') |
curl -H "Content-Type: application/json" -d @-  $URL

echo

(echo -n '{"data": "'; base64 $PIC; echo '"}') |
curl -H "Content-Type: application/json" -d @-  $URL
