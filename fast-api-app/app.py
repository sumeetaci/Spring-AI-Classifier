from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class TaskRequest(BaseModel):
    data: str

@app.post("/process-data")
async def process(request: TaskRequest):
    # Perform Python-specific work (e.g., ML inference)
    return {"result": f"Processed: {request.data}", "status": "success"}