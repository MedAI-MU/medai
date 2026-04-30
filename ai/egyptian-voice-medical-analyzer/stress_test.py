import json
import time
import os
import torch
import re
from models.nlu_engine import MedicalAnalyzer

def run_comprehensive_stress_test():
    # 1. Comprehensive list of 20 test cases (Edge Cases) to validate model robustness
    test_cases = [
        # Complex Negation cases
        "يا دكتور عندي صداع شديد بس الحمد لله معنديش لا ترجيع ولا دوخة ولا حرارتي عالية.",
        "صدري واجعني شوية بس مفيش نهجان خالص ولا في وجع بيسمع في كتفي.",
        "رجلي ورمت فجأة بس مش زرقاء ومقدرش أقول إن في ألم، هي مجرد تقيلة.",
        "حاسس بزغللة بس مفيش صداع نصفي ولا عيني حمراء ولا في غيامة عليها.",

        # Deep Egyptian Slang & Metaphors
        "حاسس إن روحي بتطلع ومبقتش قادر أصلب طولي وضربات قلبي سريعة جداً.",
        "عيني عليها غيامة ومبقتش أشوف كويس بالليل وكأني ماشي في شبورة.",
        "جسمي مهدود خالص ومفيش مجهود نهائي وأقل حركة بتخليني أقطع النفس.",
        "ودني بتصفر وبحس إن راسي بتلف بيا أول ما أقوم من على السرير.",
        "الوجع عامل زي السكاكين في بطني وبحس بمرارة فظيعة في زوري بعد الأكل.",

        # Overlapping & Multi-symptom cases
        "بقالي فترة بدخل الحمام كتير ووزني نزل النص وريقي ناشف وبحس بتنميل في صوابع إيدي.",
        "في نهجان فظيع ووجع في صدري بيسمع في فكي وضهري وحاسس إني عرقان بزيادة.",
        "ركبتي بتطقطق وورمة ومقدرش أتنيها وبحس إن عضمي كله ناشف الصبح.",
        "عندي حرقان في البول ولونه غامق وبحس بوجع في جنبي بيسمع تحت في بطني.",

        # Self-correction and Contradiction logic
        "بطني بتوجعني.. لأ قصدي صدري هو اللي واجعني وحاسس بضيق تنفس.",
        "عندي إسهال بقاله يومين.. أو تقدر تقول دخول الحمام كتير بس مفيش مغص.",
        "الوجع في كتفي الشمال.. لا اليمين، وبحس إنه بيزيد لما ببدأ أتحرك.",

        # Emergency Scenarios & Descriptive Qualifiers
        "الوجع عامل زي الكهرباء في رجلي من ورا ونازل لحد كعب رجلي تحت.",
        "اتخبطت في راسي وأغمى عليا دقيقة ومن ساعتها برجع وعيني مزغللة خالص.",
        "نص وشي سقط فجأة ومش عارف أتكلم ولا أبلع ريقي وإيدي تقيلة مش قادر أرفعها.",
        "عرقان بزيادة وجسمي بيلتش ومصدع وهفتان وعايز آكل حاجة مسكرة بسرعة."
    ]

    results = []
    total_latency = 0

    print(f"\n{'='*30} STARTING STRESS TEST (Llama-3 Edition) {'='*30}")

    # Initialize the model once to optimize memory and performance
    try:
        analyzer = MedicalAnalyzer()
    except Exception as e:
        print(f"❌ Failed to load model: {e}")
        return

    for i, text in enumerate(test_cases, 1):
        print(f"\n[Case {i}/20] Testing: {text[:50]}...")

        try:
            # 1. High-precision timing for performance profiling
            start_time = time.perf_counter()
            raw_output = analyzer.analyze(text)
            latency = time.perf_counter() - start_time
            total_latency += latency

            # 2. JSON Cleaning & Validation using Regex to ensure parsing stability
            try:
                # Extract the JSON block between the first '{' and the last '}'
                json_match = re.search(r'\{.*\}', raw_output, re.DOTALL)
                if json_match:
                    clean_json = json_match.group()
                    parsed_output = json.loads(clean_json)
                    status = "✅ Success"
                else:
                    raise ValueError("No JSON found in model response")
            except Exception:
                parsed_output = {"raw_error": raw_output}
                status = "⚠️ JSON Format Issue"

            # Store case metrics
            case_result = {
                "id": i,
                "input": text,
                "output": parsed_output,
                "latency_seconds": round(latency, 2),
                "status": status
            }
            results.append(case_result)

            # 3. Real-time console logging
            print(f"  Result: {status} | Latency: {latency:.2f}s")
            if status == "✅ Success":
                # Extract identified terms for quick preview
                terms = [s.get('clinical_term', 'N/A') for s in parsed_output.get('symptoms', [])]
                print(f"  Terms: {', '.join(terms)}")

            # 4. Clear GPU cache to maintain performance stability on the RTX 5060
            torch.cuda.empty_cache()

        except Exception as e:
            print(f"❌ Critical Error in Case {i}: {str(e)}")
            results.append({"id": i, "input": text, "error": str(e)})

    # 5. Export comprehensive results to a JSON file with UTF-8 encoding for Arabic support
    output_filename = "stress_test_results_FineTuned_V2.json"
    with open(output_filename, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=4, ensure_ascii=False)

    # Performance Summary Statistics
    avg_latency = total_latency / len(test_cases)
    print(f"\n{'='*20} STRESS TEST SUMMARY {'='*20}")
    print(f"📊 Total Cases Processed: {len(test_cases)}")
    print(f"⏱️ Average Latency: {avg_latency:.2f}s per case")
    print(f"📁 Detailed Report Saved to: {output_filename}")
    print(f"{'='*55}")

if __name__ == "__main__":
    run_comprehensive_stress_test()
