echo "--- Step 1: Registering User ---"

curl -X POST http://localhost:8080/api/users/register \
     -H "Content-Type: application/json" \
     -d "{\"username\":\"johndoe\",\"password\":\"mySecurePassword123\",\"email\":\"john@example.com\"}"

echo -e "\nRegistration complete. Press [Enter] to log in..."
read

# Fixed syntax and added jq for parsing
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d "{\"username\":\"johndoe\",\"password\":\"mySecurePassword123\"}")

echo "Response token is $RESPONSE"
# Ensure the path /ai/ matches your API docs
UPLOAD_RESPONSE=$(curl -i -X POST http://localhost:8080/ai/askWithFiles \
     -H "Authorization: Bearer $RESPONSE" \
     -F "files=@/Users/sumeetbadwal/Downloads/images/cat_pic.jpg" \
     -F "message=Generate a comma-separated list of 5-10 descriptive tags for this image. and return a JSON object with 'category' and 'tags' keys. Include objects, colors, and the overall mood in the tags.")
echo "UPLOAD_RESPONSE  is $UPLOAD_RESPONSE"

: <<'COMMENT' Sample of UPLOAD_RESPONSE is: UPLOAD_RESPONSE  is [
    {
        "image_name": "/data/prompts/johndoe/cat_pic.jpg",
        "provider": "Ollama",
        "model": "llama3.2-vision",
        "tags": "green eyes, brown fur, cat, whiskers, ears, pink nose, white whiskers.",
        "response": "green eyes, brown fur, cat, whiskers, ears, pink nose, white whiskers."
    }
]
COMMENT