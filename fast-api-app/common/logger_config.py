import logging
import sys
#from logging.handlers import RotatingFileHandler
from logging.handlers import WatchedFileHandler # Changed from RotatingFileHandler
from pythonjsonlogger import jsonlogger
log_filename='/app/logs/combined.log'
import os
log_file = os.getenv("LOGGING_FILE_NAME", "/app/logs/combined.log")

def setup_logger(name=None):
    root_logger = logging.getLogger(name) 
    # Use the root logger (empty string or logging.getLogger(None))
    # root_logger = logging.getLogger() 
    
    if not root_logger.handlers:
        root_logger.setLevel(logging.INFO)
        
        handler = WatchedFileHandler(log_file)
        # Your existing formatter...
        formatter = jsonlogger.JsonFormatter(
            fmt='%(asctime)s %(levelname)s %(name)s %(message)s',
            rename_fields={"asctime": "@timestamp", "levelname": "level", "name": "logger"}
        )
        handler.setFormatter(formatter)
        root_logger.addHandler(handler)
        
        # Also add a StreamHandler so 'docker logs' still works
        console_handler = logging.StreamHandler()
        console_handler.setFormatter(formatter)
        root_logger.addHandler(console_handler)

    return root_logger

def setup_loggerTRIED(name="fastapi_app"):
    logger = logging.getLogger(name)
    if not logger.handlers:
        logger.setLevel(logging.INFO)
        
        # Match the Spring JSON structure as closely as possible
        handler = WatchedFileHandler(log_file)
        formatter = jsonlogger.JsonFormatter(
            fmt='%(asctime)s %(levelname)s %(name)s %(message)s',
            rename_fields={"asctime": "@timestamp", "levelname": "level", "name": "logger"}
        )
        handler.setFormatter(formatter)
        logger.addHandler(handler)
    return logger

def setup_loggerold(name=__name__):
    logger = logging.getLogger(name)
    if not logger.handlers:
        logger.setLevel(logging.INFO)
        formatter = logging.Formatter('%(asctime)s [FastAPI] - %(name)s - %(levelname)s - %(message)s')

        # Console Handler
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)

        # Use WatchedFileHandler instead of RotatingFileHandler
        # Let Spring Boot handle the actual "Rotating" logic to avoid conflicts
        file_handler = WatchedFileHandler(log_filename)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)

    return logger