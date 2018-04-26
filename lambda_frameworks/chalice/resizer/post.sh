#export URL='http://localhost:8000'
export URL='https://bg8phyixb4.execute-api.us-east-1.amazonaws.com/api/'

export PIC='julien.jpg'

(echo -n '{"data": "'; base64 $PIC; echo '", "height": 32, "width": 32}') |
curl -H "Content-Type: application/json" -d @-  $URL
