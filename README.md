# Spring AI Fashion Tag Application
This application can use REST or UI.
Initially 15 images are downloaded from kaggle db for sandbox. This is done in FastAPI in bootup and runs in background. It skips the download step if files are there. It assumes you have OpenAI key and Ollama downloaded and running in local and not in docker. Download Ollama model llama3.2-vision. It first calls Ollama and fallback is OpenAI. OpenAI model testing TBD.
Execution With REST: Let you create a user who can upload pictures. User is saved in db. User can login via username and password once and then API token is generated for further REST calls. Refer or call the script: sh create_user_and_make_calls.sh
Execution with UI:
1. Go to : http://localhost:8080/ui
2. Upload any .jpg img

3. TODO implement all further steps. 
3a. Save the file to db 
3b. Show successfully save to db message to UI
3c. Show results with saved image files
3d. Add validation before saving like if the image file is not classified as cloth or garment, make it invalid and don't save
3e. Background logic supports multiple files upload, but UI does not support it. Add this in UI.
3f. Call classify on all the images with classify result and save it to db.
3g. Add more features like result pages with images based on tags, etc. Add page to add more tag or remove tag by the user

4. Add automate testing
5. Compare results with different models results if possible.


## Prerequisites
- Java 21 or higher
- Maven
- Postgres
- Open AI API key 
This examples uses OpenAI as the model provider.

Before using the AI commands, make sure you have a developer token from OpenAI.

Create an account at [OpenAI Signup](https://platform.openai.com/signup) and generate the token at [API Keys](https://platform.openai.com/account/api-keys).


Exporting an environment variable is one way to set that configuration property:

```shell
export SPRING_AI_OPENAI_API_KEY=<INSERT KEY HERE>
```
It only supports .jpg files for upload.


Main call to this application:
(A) REST call or (B) UI call

1. Verify Ollama app is running. Get Ollama models for test: curl http://localhost:11434/api/tags

(A) REST call Steps:
1. (a) Make sure Ollama is downloaded and running on machine and not docker. Download model llama3.2-vision  
   (b) or Get API key for OpenAI. Fallback AI provider is OpenAI and model GPT-5.4. These can be set in .env file 
2. docker-compose up --build
3. (a) Call : sh create_user_and_make_calls.sh ( This file will create a user with curl. Use token for next curl request and upload a given jpg path. Change this accordingly.)

(B) UI call Steps:
1. Go to : http://localhost:8080/ui
2. Upload any image file. It shows successfully uploaded, but saving is not functional yet.
3. Click on "Refresh samples" on the right side. It will display uploaded images in the bootup process. These images show in the matrix.


