# 🏥 Egyptian Voice-to-Clinical Medical Analyzer

This module is a core part of the MedAI graduation project at **Mansoura University**. It converts Egyptian Arabic speech into structured Clinical JSON reports using a state-of-the-art AI pipeline.

## 🧠 The AI Pipeline
1. **Speech-to-Text (STT)**: Fine-tuned **Whisper Large-V3** specialized in Egyptian dialects.
2. **Medical NLU**: **Llama-3 8B** (4-bit) fine-tuned on **500 custom Egyptian medical samples**.

## 💻 System Requirements
To run this engine locally, the following hardware/software is required:
* **GPU**: NVIDIA GPU with **CUDA** support and at least **8GB VRAM** (Optimized for RTX 30/40/50 series).
* **Python**: Version **3.10** or higher.
* **Storage**: Approximately 10GB of free space for model weights and environment.

## 📊 Evaluation Results
* **Accuracy**: 100% success rate in clinical symptom extraction (Based on 20 complex edge cases).
* **Latency**: ~10.11s average processing time on local hardware.
* **Features**: Support for negation detection, self-correction, and medical emergency "red flag" identification.

## 🛠️ How to Run
1. Ensure your **NVIDIA drivers** and **CUDA Toolkit** are up to date.
2. Install dependencies: `pip install -r requirements.txt`
3. Run the pipeline test: `python main_test.py`