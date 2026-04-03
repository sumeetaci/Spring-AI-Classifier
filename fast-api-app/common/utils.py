
from semantic_router import Route
#import os
#import sys
import json
import shutil
import kagglehub
from contextlib import asynccontextmanager
from fastapi import FastAPI
from pathlib import Path

import logging
logger = logging.getLogger(__name__) 

class DownloadService:
	async def my_download_function(self):
		target_dir = Path("/data/downloads")
		if target_dir.exists() and any(target_dir.iterdir()):
			print("✅ Files already exist in /data/downloads. Skipping download.")
			return  # Exit the function immediately
		target_dir.mkdir(parents=True, exist_ok=True)
		
		# 1. Use the SMALL version (approx 600MB instead of 23GB)
		dataset_slug = "paramaggarwal/fashion-product-images-small"
		
		try:
			logger.info("Downloading small fashion dataset now...")
			# Download the whole (smaller) archive to cache
			cache_path = kagglehub.dataset_download(dataset_slug)
			
			# 2. Extract exactly 15 images from the 'images' folder in the cache
			image_source = Path(cache_path) / "images"
			count = 0
			for img_file in image_source.glob("*.jpg"):
				if count >= 15: break
				shutil.copy(img_file, target_dir / img_file.name)
				count += 1
				
			logger.info(f"Done! 15 images moved to {target_dir}")
		except Exception as e:
			logger.error(f"Download failed: {e}")

class InitService:	
	# Define routes with example utterances
	static_routes = [
		Route(name="politics", utterances=["politics is great", "tell me your political opinions"]),
		Route(name="chitchat", utterances=["how's the weather?", "lovely day"])
	]
	def __init__(self, jsonargs):
		self.all_models,self.routes = self.get_all_models_and_routes(jsonargs)
	
	# This array is only for different summarize rules
	summarize_tools = ["summarizeTool", "fastSummarize", "summarize", "pdfTool", "pdfProcessor"]

	def print_dict(self, data):
		for key, value in data.items():
			print(f"\nNested Key: {key}")
			print(f"Nested Value is: {value}")
			# If the value is a list (like your 'models'), you can print its length
			if isinstance(value, list):
				print(f"Contains {len(value)} items")

	def get_all_models_and_routes(self, file_path):
		with open(file_path, 'r') as f:
			data = json.load(f)
		
		logger.info(f"Loaded {len(data['MODELS'])} model files.")
		
		# 1. Flatten the nested structure into a single list
		'''
		all_models = []
		for source_file, model_list in data["MODELS"].items():
			for model in model_list:
				# Optional: inject the source file name for traceability
				model["source_file"] = source_file
				all_models.append(model)
		
		# 2. Example: Print the results to verify
		for m in all_models:
			print(f"Provider: {m['modelProviderString']} | Model: {m['resourceNameString']}")
		'''
		all_models = self.load_models(data)
		route_objects = self.load_routes(data)
		return all_models, route_objects 

	def load_models(self, data):
		all_models = []
		for source_file, model_list in data["MODELS"].items():
			for model in model_list:
				# Optional: inject the source file name for traceability
				model["source_file"] = source_file
				all_models.append(model)
		for m in all_models:
			logger.info(f"Provider: {m['modelProviderString']} | Model: {m['resourceNameString']}")
		return all_models
			   
				
	def load_routesOLD(self, data):
		for key, value in data.items():
			logger.info(f"\nKey: {key}")
			logger.info(f"Value Type: {type(value)}")
			if key == "ROUTES":
				#global routes_args
				routes_args = value
			
			# If the value is a list (like your 'models'), you can print its length
			if isinstance(value, list):
				logger.info(f"Contains {len(value)} items")
			if isinstance(value, dict):
				self.print_dict(value)
		route_objects = []
		if isinstance(routes_args, dict):
			for route_name, utterances in routes_args.items():
				# Create a Route object for each key-value pair
				route_objects.append(Route(name=route_name, utterances=utterances))
		else:
			# Fallback to your static_routes if JSON parsing failed
			route_objects = self.static_routes
		for route in route_objects:
			logger.info(f"route name is {route.name} and route utterances is {route.utterances}")
		return route_objects
	
	def load_routes(self,data):
		route_objects = []
		logger.info(f"Inside load_routes")
		# Extract the dictionary nested under "ROUTES"
		routes_data = data["ROUTES"]
		#routes_data = data.get("ROUTES", {})

		if routes_data:
			for route_name, utterances in routes_data.items():
				# route_name is "politics", utterances is the list of strings
				route_objects.append(Route(name=route_name, utterances=utterances))
		else:
			route_objects = self.static_routes

		for route in route_objects:
			logger.info(f"route name is {route.name} and route utterances is {route.utterances}")
		return route_objects
	
	def get_endpoint_for_model(model_id: str):
		# Pro/Reasoning models use Responses API
		if "pro" in model_id.lower() or "reasoning" in model_id.lower():
			return "responses"
		
		# Instruct models use legacy Completions
		if "-instruct" in model_id.lower():
			return "completions"
			
		# Most others (gpt-4, gpt-5.4, gpt-3.5-turbo) use Chat
		return "chat"
