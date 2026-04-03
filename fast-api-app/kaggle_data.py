import os
import shutil
import kagglehub
from contextlib import asynccontextmanager
from fastapi import FastAPI
from pathlib import Path
import asyncio
app = FastAPI()

# Mapped to ./shared-data in your docker-compose

DOWNLOAD_DIR = Path("/data/downloads")

def download_ten_images(dataset_slug: str):
    DOWNLOAD_DIR.mkdir(parents=True, exist_ok=True)
    
    # 1. Download dataset (defaults to ~/.cache/kagglehub)
    cache_path = kagglehub.dataset_download(dataset_slug)
    
    # 2. Extract exactly 10 image files
    count = 0
    image_extensions = {'.jpg', '.jpeg', '.png'}
    
    for root, _, files in os.walk(cache_path):
        for file in files:
            if count >= 10:
                break
                
            file_path = Path(root) / file
            if file_path.suffix.lower() in image_extensions:
                # Move to the shared volume
                shutil.copy(file_path, DOWNLOAD_DIR / file)
                count += 1
        if count >= 10:
            break
            
    print(f"Done! 10 images moved to {DOWNLOAD_DIR}")

'''
@app.post("/fetch-sample")
async def fetch_sample(dataset: str, background_tasks: BackgroundTasks):
    """
    Triggers download. Example dataset: "paramaggarwal/fashion-product-images-dataset"
    """
    background_tasks.add_task(download_ten_images, dataset)
    return {"message": "Started downloading 10 images in background", "target": str(DOWNLOAD_DIR)}
'''
async def my_download_function():
     # --- STARTUP LOGIC ---
    print("FastAPI starting up: Downloading Kaggle images...")
    DOWNLOAD_DIR.mkdir(parents=True, exist_ok=True)
    
    # Only download if the directory is empty to avoid repeats on restarts
    if not any(DOWNLOAD_DIR.iterdir()):
        dataset_slug = "paramaggarwal/fashion-product-images-dataset"
        cache_path = kagglehub.dataset_download(dataset_slug)
        
        count = 0
        image_extensions = {'.jpg', '.jpeg', '.png'}
        for root, _, files in os.walk(cache_path):
            for file in files:
                if count >= 10: break
                file_path = Path(root) / file
                if file_path.suffix.lower() in image_extensions:
                    shutil.copy(file_path, DOWNLOAD_DIR / file)
                    count += 1
            if count >= 10: break
        print(f"Startup complete: {count} images ready in {DOWNLOAD_DIR}")
    else:
        print("Images already exist in /data/downloads, skipping download.")

@asynccontextmanager
async def lifespan(app: FastAPI):
    asyncio.create_task(my_download_function())
    yield
    # --- SHUTDOWN LOGIC (Optional) ---
    # print("FastAPI shutting down...")

app = FastAPI(lifespan=lifespan)