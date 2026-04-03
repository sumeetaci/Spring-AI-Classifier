FROM python:3.11-slim

# 1. Use Debian/Ubuntu commands for user creation (UID 1000)
RUN groupadd -g 1000 appuser && \
    useradd -u 1000 -g appuser -m appuser

WORKDIR /app
RUN chown appuser:appuser /app

# 2. Install minimal build tools (often needed for LangChain/PyYAML)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
# Pip will now find pre-compiled wheels for PyMuPDF and install in seconds
RUN pip install --no-cache-dir -r requirements.txt

COPY --chown=appuser:appuser . .

USER appuser
EXPOSE 8000
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]