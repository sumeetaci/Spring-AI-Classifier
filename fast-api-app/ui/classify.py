import os
import logging
from pathlib import Path
from common.logger_config import setup_logger
import time
import ollama
from openai import OpenAI 
from fastapi import Query
import requests
import base64
from routeAndProcess import route_and_process
DOWNLOAD_DIR = Path("/data/downloads")
logger = setup_logger()
logger.info(f"OpenAI API Key found: {'Yes' if 'OPENAI_API_KEY' in os.environ else 'No'}")
logger.info(f"Ollama URL found: {'Yes' if 'OLLAMA_URL' in os.environ else 'No'}")
logger.info(f"OpenAI model found: {'Yes' if 'OPENAI_MODEL' in os.environ else 'No'}")
# 1. Initialize once; handle Docker host if needed
ollamaClient = ollama.Client(host=os.getenv("OLLAMA_URL", "http://localhost:11434"))
# 2. Robust model selection
ollamaModel = os.getenv("OLLAMA_MODEL") or "llama3.2-vision"
open_api_key = os.getenv("OPENAI_API_KEY")
openAIModel = os.getenv("OPENAI_MODEL","GPT-5.4")
download_dir="/data/downloads"
base_dir = Path(__file__).resolve().parent

class prediction_tool:
    def __init__(self, file_name, prompt):
        # 1. Call the parser first to get the values
        self.prompt_query = prompt 
        self.image_path = os.path.join(download_dir, file_name)
        outfilename=f"{file_name}out_response.json"
        self.output_file = os.path.join(download_dir, outfilename)
        

    def getOpenAIClientResponse(self, img_path):
        base64_image=self.get_image_bytes(img_path)
        content_list = self.get_content_for_ai_client(self.prompt_query, base64_image)
        client = OpenAI(open_api_key)
        response = client.chat.completions.create(
                model=openAIModel, # Cost-effective vision model
                messages=content_list,
                max_tokens=300
            )
        return response

    def getOllamaAIClient(self, img_path):
        logger.info(f"In getOllamaAIClient")
        response = ollamaClient.chat(
            model=os.environ.get("OLLAMA_MODEL","llama3.2-vision"),
            messages=[{'role': 'user', 'content': self.prompt_query, 'images': [img_path]}]
        )
        logger.info(f"Out getOllamaAIClient")
        return response
    
    def get_image_bytes(self, img_path):
        with open(img_path, "rb") as image_file:
                base64_image = base64.b64encode(image_file.read()).decode('utf-8')
        return base64_image
    
    def get_ai_response(self,img_path):
        ollama_host = os.getenv("OLLAMA_URL")
        # Add more if needed. Include objects, colors, and the overall mood in the tags.
        logger.info(f"Starting get_ai_response")
        # 1. Try Ollama First
        try:
            # Quick check if Ollama is running
            requests.get(ollama_host, timeout=1)
            response = self.getOllamaAIClient(img_path)
            logger.info(f"Ending get_ai_response with Ollama {response}")
            return response['message']['content'], "Ollama"

        except (requests.exceptions.RequestException, Exception):
            # 2. Fallback to OpenAI
            logger.info(f"Starting OpenAI ")
            if not open_api_key:
                return "Error: Both Ollama and OpenAI API Key are missing.", None
            '''
            openai_client = OpenAI(open_api_key)
            base64_image=self.get_image_bytes(img_path)
            content_list = self.get_content_for_ai_client(prompt, base64_image)
            response = openai_client.chat.completions.create(
                model=openAIModel, # Cost-effective vision model
                messages=content_list,
                max_tokens=300
            )
            '''
            response = self.getOpenAIClientResponse(img_path)
            logger.info(f"Ending get_ai_response with OpenAI")
            return response.choices[0].message.content, "OpenAI"
    
    def get_content_for_ai_client(self,prompt, base64_image):
        content_list = []
        img_item = {
            "type": "image_url",
            "image_url" : {
            "url": f"data:image/jpeg;base64,{base64_image}"
            }
            }

        text_item = {
            "type": "text",
            "text": prompt
        }
        content_list.append(text_item)
        content_list.append(img_item)
        return content_list


    def classify_image(self,filename: str = Query(..., description="The name of the file in /data/downloads")):
        """
        Reads an image from the shared volume and classifies it using Ollama.
        """
        # 1. Construct the path inside the container
        image_path = filename # os.path.join("/data/downloads", filename)
        
        # 2. Check if the file actually exists
        if not os.path.exists(image_path):
            logger.error(f"Classification failed: File {image_path} not found.")
            return {"error": f"File {filename} not found in shared volume."}

        try:
            logger.info(f"Classifying image: {filename} using Ollama...")
            # 3. Call Ollama
            # Note: Ensure OLLAMA_HOST is set if Ollama is on the host machine
            '''
            response = ollama.chat(
                model='llava', 
                messages=[{
                    'role': 'user',
                    'content': 'Describe this fashion item. Include category, color, and material if possible. Keep it to one sentence.',
                    'images': [image_path]
                }]
            )
            '''
            response, provider = self.get_ai_response(filename)
            #classification_text = response #['message']['content']
            classification_item={
                "response": response,
                "provider" : provider
            }
            logger.info(f"Classification done by {provider} and is successful: {response}")
            #return {"result": classification_text}
            return classification_item #response, provider

        except Exception as e:
            logger.error(f"Ollama or OpenAI error: {str(e)}")
            # return {"error": "Failed to communicate with AI model."}
            return "Failed to communicate with AI model."