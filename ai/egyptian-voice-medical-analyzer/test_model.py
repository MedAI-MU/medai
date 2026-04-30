import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, BitsAndBytesConfig
from peft import PeftModel

# 1. Configuration Paths
# Ensure the adapter folder is located in the same directory as this script
base_model_name = "unsloth/llama-3-8b-bnb-4bit"
adapter_path = "./Llama3_Egyptian_Medic_Final"

# 2. Configure 4-bit Quantization for Local Inference
# Optimized for high-performance hardware with 8GB VRAM
quant_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_compute_dtype=torch.float16,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_use_double_quant=True,
)

# 3. Initialize Tokenizer and Base Model
print("⏳ Loading Base Model (Llama-3 8B)...")
tokenizer = AutoTokenizer.from_pretrained(base_model_name)
model = AutoModelForCausalLM.from_pretrained(
    base_model_name,
    quantization_config=quant_config,
    device_map="auto"
)

# 4. Integrate LoRA Adapters
# These adapters contain the knowledge fine-tuned from the 500 Egyptian medical samples
print("🚀 Merging Fine-tuned Egyptian Medical Adapters...")
model = PeftModel.from_pretrained(model, adapter_path)
model.eval()

# 5. Alpaca Prompt Template
# Standard template used to maintain consistency between training and inference
alpaca_prompt = """Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request.

### Instruction:
Extract medical symptoms from Egyptian Arabic slang.

### Input:
{}

### Response:
"""

# 6. Inference Function
def get_diagnosis(query):
    """
    Tokenizes the input query, generates a response using the model,
    and extracts the final JSON response.
    """
    inputs = tokenizer(alpaca_prompt.format(query), return_tensors="pt").to("cuda")
    with torch.no_grad():
        outputs = model.generate(
            **inputs,
            max_new_tokens=256,
            pad_token_id=tokenizer.eos_token_id
        )

    decoded_output = tokenizer.decode(outputs[0], skip_special_tokens=True)

    # Extract the response section from the full Alpaca output
    if "### Response:" in decoded_output:
        return decoded_output.split("### Response:")[1].strip()
    return decoded_output

# 7. Execution Example (Live Inference)
if __name__ == "__main__":
    print("\n--- Medic AI System Test Drive ---")

    # Sample Egyptian medical complaint for testing
    user_input = "يا دكتور روحي بتتسحب مني وعظمي مكسر حتت، بس معنديش سخونية."

    result = get_diagnosis(user_input)

    print(f"\nUser Input: {user_input}")
    print(f"Extraction Results:\n{result}")
