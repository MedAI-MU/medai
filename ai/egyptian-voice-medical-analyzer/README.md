# 🏥 Egyptian Voice-to-Clinical Medical Analyzer

This module is a core part of the MedAI graduation project at Mansoura University. It converts Egyptian Arabic speech into structured Clinical JSON reports using a state-of-the-art AI pipeline.

## 🚀 Quick Start Guide

To get the engine running on your local machine in less than 5 minutes:

1. **Clone & Navigate**:
   ```powershell
   cd ai/egyptian-voice-medical-analyzer
   ```

2. **Setup Environment**:
   ```powershell
   python -m venv ai_env
   .\ai_env\Scripts\activate
   ```

3. **Install Dependencies**:
   ```powershell
   pip install -r requirements.txt
   ```

4. **Run Inference**:
   ```powershell
   python main_test.py
   ```

---

## 🛠️ Hardware & Torch Compatibility (Important)

> [!IMPORTANT]
> The current `requirements.txt` is pre-configured for **NVIDIA Blackwell (RTX 50 series)** architecture using **CUDA 13.0**.

If you are running this on different hardware (e.g., RTX 20/30 series or CPU), the Torch version in the requirements file might not be compatible:

* **For Older GPUs**: You may need to change the `--extra-index-url` to `cu118` or `cu121` and reinstall Torch.
* **For CPU Only**: Remove the index URL and install the standard `torch` package.
* **Check Your Version**: Visit [pytorch.org](https://pytorch.org/) to find the exact command for your specific CUDA version.

---

## 🧠 The AI Pipeline

* **Speech-to-Text (STT)**: Fine-tuned Whisper Large-V3 specialized in Egyptian dialects.
* **Medical NLU**: Llama-3 8B (4-bit) fine-tuned on 500 custom Egyptian medical samples.

## 💻 System Requirements

* **GPU**: NVIDIA GPU with CUDA support and at least 8GB VRAM (Optimized for Blackwell/Ada Lovelace).
* **Python**: Version 3.10 or higher.
* **Storage**: ~10GB for model weights and environment.

## 📊 Evaluation Results

* **Accuracy**: 100% success rate in clinical symptom extraction (Based on 20 complex edge cases).
* **Latency**: ~48s total pipeline time on local RTX 5060.
* **Features**: Support for negation detection, self-correction, and medical "red flags".