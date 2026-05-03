import json
import time
import os
import gc
import asyncio
import tempfile
import torch
from fastapi import FastAPI, UploadFile, File, HTTPException, BackgroundTasks
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager

from models.stt_engine import EgyptianSTT
from models.nlu_engine import MedicalAnalyzer

# --- Global Concurrency Lock ---
# Ensures only one analysis happens at a time due to VRAM constraints.
GPU_LOCK = asyncio.Lock()

def clear_gpu():
    """Utility function to completely clear the GPU VRAM cache."""
    torch.cuda.empty_cache()
    gc.collect()

def run_analysis_pipeline(audio_path: str) -> dict:
    """
    Synchronous pipeline exactly as provided in main_test.py, but returning a dict.
    Runs STT, unloads, then runs NLU, then unloads.
    """
    print("\n" + "="*60)
    print("🚀 STARTING OPTIMIZED PIPELINE (SEQUENTIAL MODE)")
    print("="*60)

    # --- Stage 1: Speech-to-Text (STT) ---
    print(f"\n🎙️ Stage 1: Transcribing audio...")
    stt = EgyptianSTT() # Model is loaded into VRAM here
    start_time_stt = time.time()

    try:
        patient_text = stt.transcribe(audio_path)
    except Exception as e:
        del stt
        clear_gpu()
        raise Exception(f"STT Error: {str(e)}")

    stt_latency = time.time() - start_time_stt

    print(f"[STT Output]: {patient_text}")
    print(f"⏱️ STT Latency: {stt_latency:.2f}s")

    # Completely clear Whisper from GPU before loading Llama-3
    del stt
    clear_gpu()
    print("🧹 GPU Cleared for NLU processing...")

    if "Error" in patient_text:
        raise Exception(f"Pipeline halted due to STT error: {patient_text}")

    # --- Stage 2: Natural Language Understanding (NLU) ---
    print("\n🧠 Stage 2: Analyzing with Fine-tuned Llama-3...")
    analyzer = MedicalAnalyzer() # Model is loaded into VRAM here
    start_time_nlu = time.time()

    try:
        report_raw = analyzer.analyze(patient_text)
    except Exception as e:
        del analyzer
        clear_gpu()
        raise Exception(f"NLU Error: {str(e)}")

    nlu_latency = time.time() - start_time_nlu

    # Clear the NLU model from GPU after completion
    del analyzer
    clear_gpu()

    report_data = None
    try:
        # Locate the JSON structure within the model's raw output
        json_start = report_raw.find('{')
        json_end = report_raw.rfind('}') + 1
        if json_start != -1:
            report_data = json.loads(report_raw[json_start:json_end])
        else:
            raise ValueError("No JSON found in model output")
    except Exception as e:
        print(f"❌ Analysis Parsing Error: {report_raw}")
        raise Exception(f"Failed to parse NLU output: {report_raw}")

    return {
        "transcription": patient_text,
        "clinical_analysis": report_data,
        "performance": {
            "stt_latency_sec": round(stt_latency, 2),
            "nlu_latency_sec": round(nlu_latency, 2),
            "total_pipeline_sec": round(stt_latency + nlu_latency, 2)
        }
    }


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Setup: ensure GPU is clear before starting
    clear_gpu()
    yield
    # Teardown
    clear_gpu()

app = FastAPI(
    title="Egyptian Voice Medical Analyzer API",
    description="Microservice to sequentially run STT and NLU within VRAM limits.",
    version="1.0.0",
    lifespan=lifespan
)


@app.post("/api/v1/analyze-audio")
async def analyze_audio(file: UploadFile = File(...)):
    """
    Accepts an audio file and processes it via the ML pipeline.
    Uses an asyncio Mutex Lock to ensure exactly one job runs at a time.
    """
    if not file.filename.endswith((".wav", ".mp3", ".m4a", ".ogg")):
        raise HTTPException(status_code=400, detail="Invalid file type. Please upload an audio file.")

    # Save the uploaded file temporarily
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
        content = await file.read()
        tmp.write(content)
        tmp_path = tmp.name

    try:
        # Acquire the GPU lock so we don't blow up VRAM
        print("Acquiring GPU lock...")
        async with GPU_LOCK:
            print("GPU lock acquired. Running pipeline...")
            # Run the synchronous ML pipeline in a separate thread so it doesn't block the asyncio event loop
            # This allows FastAPI to still accept incoming connections and queue them up
            result = await asyncio.to_thread(run_analysis_pipeline, tmp_path)

        return JSONResponse(content=result)

    except Exception as e:
        print(f"Error during analysis: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        # Always clean up the temporary file
        if os.path.exists(tmp_path):
            os.remove(tmp_path)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("api:app", host="0.0.0.0", port=8000, reload=True)
