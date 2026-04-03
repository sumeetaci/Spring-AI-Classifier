import os
import sys
import json
import yaml
import argparse
import asyncio
import httpx  # Faster for async than 'requests'
from pathlib import Path
import ollama
from ollama import Client
import asyncio
import requests
import base64
from ollama import AsyncClient
# Initialize the logger for this specific file
#logger = setup_logger("route_and_process")
from openai import OpenAI
import logging

logger = logging.getLogger(__name__)
# logger = logging.getLogger("route_and_process")
logger.info(f"OpenAI API Key found: {'Yes' if 'OPENAI_API_KEY' in os.environ else 'No'}")
logger.info(f"Ollama URL found: {'Yes' if 'OLLAMA_URL' in os.environ else 'No'}")
logger.info(f"OpenAI model found: {'Yes' if 'OPENAI_MODEL' in os.environ else 'No'}")
# 1. Initialize once; handle Docker host if needed
ollamaClient = ollama.Client(host=os.getenv("OLLAMA_URL", "http://localhost:11434"))
# 2. Robust model selection
ollamaModel = os.getenv("OLLAMA_MODEL") or "llama3.2-vision"
open_api_key = os.getenv("OPENAI_API_KEY")
openAIModel = os.getenv("OPENAI_MODEL","GPT-5.4")

base_dir = Path(__file__).resolve().parent
rules_path = ""

class route_and_process:
    def __init__(self, prompt_dir, prompt):
        
        self.prompt_query = prompt # args.prompt
        self.output_file = f"{prompt_dir}out_response.json" #args.output_file
        self.prompt_dir = prompt_dir

    def write_response_to_file(self):
        self.generate_image_tags_from_dir()
        #TODO Add model and tool for this query
        logger.info(f"Wrote to file::::: {self.output_file}")
        logger.info(f"In write_response_to_file ends ")

    def getOpenAIClientResponse(self, prompt):
        client = OpenAI(open_api_key)
        response = client.completions.create(
                model=os.environ.get("OPENAI_MODEL", 'GPT-5.4'),
                prompt=prompt
            )
        return response
    
    def getOllamaAIClient(self, prompt, image_path):
        response = ollama.Client().chat(
            model=os.environ.get("OLLAMA_MODEL","llama3.2-vision"),
            messages=[{'role': 'user', 'content': prompt, 'images': [image_path]}]
        )
        return response
        

    def get_all_jpg(self):
        image_files=[]
        for file in os.listdir(self.prompt_dir):
            if file.lower().endswith(".jpg"): # Add more extensions as needed
                image_files.append(os.path.join(self.prompt_dir, file))
        logger.info(f"Image files count is: {len(image_files)}")        
        return image_files  # No JPG found
    
    def get_image_bytes(self, img_path):
        with open(img_path, "rb") as image_file:
                base64_image = base64.b64encode(image_file.read()).decode('utf-8')
        return base64_image
    
    def get_ai_response(self, prompt, img_path):
        ollama_host = os.getenv("OLLAMA_URL")
        # Add more if needed. Include objects, colors, and the overall mood in the tags.
        prompt_local = "Generate a comma-separated list of 5-10 descriptive tags for this image and return a JSON object with 'category' and 'tags' keys."
        logger.info(f"Starting get_ai_response")
        # 1. Try Ollama First
        try:
            # Quick check if Ollama is running
            requests.get(ollama_host, timeout=1)
            response = ollamaClient.chat(
                model=ollamaModel,
                messages=[{'role': 'user', 'content': prompt_local, 'images': [img_path]}]
            )
            logger.info(f"Ending get_ai_response with Ollama")
            return response['message']['content'], "Ollama"

        except (requests.exceptions.RequestException, Exception):
            # 2. Fallback to OpenAI
            logger.info(f"Starting OpenAI ")
            if not open_api_key:
                return "Error: Both Ollama and OpenAI API Key are missing.", None

            openai_client = OpenAI(open_api_key)
            base64_image=self.get_image_bytes(img_path)
            content_list = self.get_content_for_ai_client(prompt, base64_image)
            response = openai_client.chat.completions.create(
                model=openAIModel, # Cost-effective vision model
                messages=content_list,
                max_tokens=300
            )
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
    
    def generate_image_tags_from_dir(self):
        # Prompt the model to return ONLY tags
        image_paths = self.get_all_jpg()
        logger.info(f"Write to file : {self.output_file}")
        image_data = []
        for img_path in image_paths:
                response, provider = self.get_ai_response(self.prompt_query, img_path)
                #image_tags[img_path] = response
                logger.info(f"Response from AI is : {response} ")
                image_data.append(self.log_to_json(provider, img_path, response ))
                
        with open(self.output_file, "w") as f:
            json.dump(image_data, f, indent=4)
        logger.info("Wrote in file all the tags")
        #return image_tags

    def log_to_json(self, provider, image_name, content):
        logger.info(f"Coming in log_to_json {image_name} {content} ")
        if "Ollama" == provider:
            model = ollamaModel
        if "OpenAI" == provider:
            model = openAIModel
        entry = {
            "image_name": image_name,
            "provider": provider,
            "model": model,
            "tags": content
           }
        return entry

# Usage
#tags = generate_image_tags('path/to/your/image.jpg')
#print(f"Generated Tags: {tags}")


if __name__ == "__main__":
    summarizer = route_and_process()
    print("Inside python script....")
    summarizer.write_response_to_file()
