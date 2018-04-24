export URL='http://localhost:8000'
export PIC='julien.jpg'

curl -X POST -H "Content-Type: application/json" -d '{
    "width": 32, "height": 32, "data": "'"$( base64 -i julien.jpg )"'"
}' $URL
