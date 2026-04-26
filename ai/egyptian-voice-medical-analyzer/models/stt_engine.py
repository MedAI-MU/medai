import torch
import librosa
from transformers import AutoModelForSpeechSeq2Seq, AutoProcessor, pipeline

class EgyptianSTT:
    def __init__(self):
        """
        Initializes the Egyptian Speech-to-Text engine using a fine-tuned 
        Whisper large-v3 model specialized in Egyptian Arabic.
        """
        # Fine-tuned model ID for Egyptian Arabic
        model_id = "AbdelrahmanHassan/whisper-large-v3-egyptian-arabic"
        
        # Check for CUDA availability (Optimized for RTX 5060 hardware)
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.dtype = torch.float16 if self.device == "cuda" else torch.float32

        print(f"Loading Egyptian AI Engine on {self.device}...")

        # Explicitly loading components helps prevent potential KeyError issues in Transformers
        self.processor = AutoProcessor.from_pretrained(model_id)
        self.model = AutoModelForSpeechSeq2Seq.from_pretrained(
            model_id, 
            torch_dtype=self.dtype, 
            low_cpu_mem_usage=True, 
            use_safetensors=True
        ).to(self.device)

        # Build the inference pipeline with specific model and processor components
        self.pipe = pipeline(
            "automatic-speech-recognition",
            model=self.model,
            tokenizer=self.processor.tokenizer,
            feature_extractor=self.processor.feature_extractor,
            chunk_length_s=30, # Essential for Whisper models to manage long audio durations
            torch_dtype=self.dtype,
            device=self.device,
        )

    def transcribe(self, audio_path):
        """
        Loads an audio file and processes it through the pipeline to return 
        the transcribed text in Egyptian Arabic.
        """
        try:
            print(f"Reading audio file: {audio_path}")
            # Load audio at the standard 16kHz sampling rate required by Whisper
            audio, sr = librosa.load(audio_path, sr=16000)

            print("AI is transcribing (Egyptian Mode)...")
            
            # Specify language and task to ensure the highest transcription accuracy
            result = self.pipe(
                audio, 
                generate_kwargs={"language": "arabic", "task": "transcribe"}
            )
            return result["text"]
        except Exception as e:
            # Return raw error message if the transcription fails
            return f"Error: {str(e)}"