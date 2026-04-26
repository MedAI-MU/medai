import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, BitsAndBytesConfig
from peft import PeftModel
import json

class MedicalAnalyzer:
    def __init__(self, adapter_path="./Llama3_Egyptian_Medic_Final"):
        """
        Initializes the Medical NLU engine by loading the base Llama-3 model
        and merging the fine-tuned Egyptian Medical Adapters.
        """
        print(f"⏳ Loading Fine-tuned Llama-3 (500 Samples Edition)...")
        self.base_model_name = "unsloth/llama-3-8b-bnb-4bit"

        # 1. Configure 4-bit Quantization for optimized VRAM usage on RTX 5060
        quant_config = BitsAndBytesConfig(
            load_in_4bit=True,
            bnb_4bit_compute_dtype=torch.float16,
            bnb_4bit_quant_type="nf4",
            bnb_4bit_use_double_quant=True,
        )

        # 2. Load the Tokenizer
        self.tokenizer = AutoTokenizer.from_pretrained(self.base_model_name)

        # 3. Load the Base Model and force it onto GPU 0 to prevent CPU offloading
        model = AutoModelForCausalLM.from_pretrained(
            self.base_model_name,
            quantization_config=quant_config,
            device_map={"": 0},
            torch_dtype=torch.float16,
        )

        # 4. Integrate the Fine-tuned LoRA Adapters (The 500-sample knowledge base)
        print(f"🚀 Integrating Egyptian Medical Adapters from: {adapter_path}")
        self.model = PeftModel.from_pretrained(model, adapter_path)
        self.model.eval()

        # Define the Alpaca prompt template used during the Colab training phase
        self.alpaca_prompt = """Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request.

### Instruction:
Extract medical symptoms from Egyptian Arabic slang.

### Input:
{}

### Response:
"""
        print("✅ Medical Brain Loaded Successfully!")

    def analyze(self, patient_text):
        """
        Converts raw Egyptian slang text into a structured Clinical JSON report.
        """
        # Prepare inputs for the model
        inputs = self.tokenizer(
            self.alpaca_prompt.format(patient_text),
            return_tensors="pt"
        ).to("cuda")

        # Generate the clinical response
        with torch.no_grad():
            outputs = self.model.generate(
                **inputs,
                max_new_tokens=256,
                pad_token_id=self.tokenizer.eos_token_id,
                temperature=0.1,    # Set low for high deterministic accuracy
                do_sample=True
            )

        # Decode and extract only the Response section
        decoded = self.tokenizer.decode(outputs[0], skip_special_tokens=True)

        if "### Response:" in decoded:
            response_json = decoded.split("### Response:")[1].strip()
            # Clean up the output by removing the end-of-text token if present
            if "<|end_of_text|>" in response_json:
                response_json = response_json.split("<|end_of_text|>")[0].strip()
            return response_json

        return decoded
