from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from routeAndProcess import route_and_process
#from typing import Dict
from common.logger_config import setup_logger
import os
import logging
from ui.classify import prediction_tool

from contextlib import asynccontextmanager
from fastapi import FastAPI
from pathlib import Path
from fastapi import Query
from common.utils import DownloadService
import asyncio
DOWNLOAD_DIR = Path("/data/downloads")
logger = setup_logger()
app = FastAPI()
print(f"DEBUG: Kaggle User is {os.getenv('KAGGLE_USERNAME')}")
class DirectoryRequest(BaseModel):
    path: str
    message: str

class SessionDirectoryRequest(BaseModel):
    path: str
    message: str
    conversationId: str

@app.on_event("startup")
async def startup_event():
    # 2. Get the handler we just created (the JSON File handler)
    file_handler = logger.handlers[0]
    
    # 3. Attach that SAME handler to Uvicorn so they share the file
    for name in ["uvicorn", "uvicorn.error", "uvicorn.access"]:
        uv_logger = logging.getLogger(name)
        uv_logger.handlers = [file_handler]
        uv_logger.propagate = False
    
    logger.info("Custom App Logger is now active!")
    service = DownloadService()
    asyncio.create_task(service.my_download_function())

# This method is for REST call
@app.post("/process-directory")
#async def process_directory(request: DirectoryRequest): #(data: dict): # Accept any JSON object
def process_directory(data: dict): # Accept any JSON object
    logger.debug(f"Received data: {data}")
    logger.info(f"Received data path: {data.get('path')}")
    logger.info(f"Received data message :{data.get('message')}")
    summarizer = route_and_process(data.get("path"), data.get("message"))
    logger.debug("Inside python script....")
    response = summarizer.write_response_to_file()
    logger.debug(f"Response written by python script....{response}")
    return {"status": "received", "data": data}

@app.get("/classify")
def process_ui_file(filename: str = Query(..., description="The name of the file in /data/downloads")):
    logger.info(f"In classify, received filename : {filename}")
    full_path = os.path.join("/data/downloads", filename)
    print(f"In classify, received filename : {filename}")
    classify_prompt='Describe this fashion item. Include category, color, and material if possible. Keep it to one sentence.'
    try:
        classifier=prediction_tool(full_path, classify_prompt)
        classification_content=classifier.classify_image(full_path) #, classify_prompt)
        logger.info(f"Model response is :{classification_content}")
        return {"result": classification_content}
    except Exception as e:
        logger.error(f"CRASH: {str(e)}")
        return {"error": str(e)}, 500
