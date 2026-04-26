import json
import time
import os
import gc
import torch
from models.stt_engine import EgyptianSTT
from models.nlu_engine import MedicalAnalyzer

def clear_gpu():
    """Utility function to completely clear the GPU VRAM cache."""
    torch.cuda.empty_cache()
    gc.collect()

def main():
    # Path to the input audio file
    audio_path = "test_audio/test5.wav"

    if not os.path.exists(audio_path):
        print(f"❌ Error: Audio file '{audio_path}' not found!")
        return

    print("\n" + "="*60)
    print("🚀 INITIALIZING OPTIMIZED PIPELINE (SEQUENTIAL MODE)")
    print("="*60)

    # --- Stage 1: Speech-to-Text (STT) ---
    print(f"\n🎙️ Stage 1: Transcribing {audio_path}...")
    stt = EgyptianSTT() # Model is loaded into VRAM here
    start_time_stt = time.time()
    patient_text = stt.transcribe(audio_path)
    stt_latency = time.time() - start_time_stt

    print(f"[STT Output]: {patient_text}")
    print(f"⏱️ STT Latency: {stt_latency:.2f}s")

    # Completely clear Whisper from GPU before loading Llama-3
    del stt
    clear_gpu()
    print("🧹 GPU Cleared for NLU processing...")

    if "Error" not in patient_text:
        # --- Stage 2: Natural Language Understanding (NLU) ---
        print("\n🧠 Stage 2: Analyzing with Fine-tuned Llama-3...")
        analyzer = MedicalAnalyzer() # Model is loaded into VRAM here
        start_time_nlu = time.time()
        report_raw = analyzer.analyze(patient_text)
        nlu_latency = time.time() - start_time_nlu

        # Display final results
        print("\n" + "="*60)
        print("🏥 FINAL MEDICAL REPORT")
        print("="*60)

        try:
            # Locate the JSON structure within the model's raw output
            json_start = report_raw.find('{')
            json_end = report_raw.rfind('}') + 1
            if json_start != -1:
                report_data = json.loads(report_raw[json_start:json_end])
                # Print formatted JSON with Arabic support
                print(json.dumps(report_data, indent=4, ensure_ascii=False))

            # Print performance analytics
            print("\n" + "-"*60)
            print(f"📊 PERFORMANCE SUMMARY:")
            print(f"   - STT Latency: {stt_latency:.2f}s (GPU Mode)")
            print(f"   - NLU Latency: {nlu_latency:.2f}s (GPU Mode)")
            print(f"   - Total Pipeline Time: {stt_latency + nlu_latency:.2f}s")
            print("-" * 60)

        except Exception as e:
            print(f"❌ Analysis Parsing Error: {report_raw}")

        # Clear the NLU model from GPU after completion
        del analyzer
        clear_gpu()
    else:
        print("⚠️ Pipeline halted due to STT error.")

if __name__ == "__main__":
    main()
