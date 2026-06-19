import tensorflow as tf
import os

def convert_h5_to_tflite(h5_model_path, output_tflite_path):
    """
    Converts a Keras .h5 model to a TensorFlow Lite .tflite model
    using Float16 Quantization to reduce the size by ~50% and speed up mobile inference.
    """
    print(f"Loading Keras model from: {h5_model_path} ...")
    if not os.path.exists(h5_model_path):
        print(f"Error: Model file '{h5_model_path}' not found!")
        return

    # Reconstruct the exact DenseNet121 model architecture matching the assignment
    print("Reconstructing DenseNet121 model architecture (320x320x3 -> 14 pathologies)...")
    base_model = tf.keras.applications.DenseNet121(weights=None, include_top=False, input_shape=(320, 320, 3))
    x = base_model.output
    x = tf.keras.layers.GlobalAveragePooling2D()(x)
    predictions = tf.keras.layers.Dense(14, activation="sigmoid")(x)
    model = tf.keras.models.Model(inputs=base_model.input, outputs=predictions)

    print(f"Loading weights from: {h5_model_path} ...")
    model.load_weights(h5_model_path)

    print("Initiating TFLite conversion with Float16 Quantization...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)

    # Apply standard dynamic-range optimization
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    # Set target precision to Float16 (supported by most mobile GPUs and CPUs very efficiently)
    converter.target_spec.supported_types = [tf.float16]

    tflite_quant_model = converter.convert()

    print(f"Saving optimized TFLite model to: {output_tflite_path} ...")
    with open(output_tflite_path, 'wb') as f:
        f.write(tflite_quant_model)

    # Auto-copy to Android assets folder
    android_assets_path = "mobile/MedAI/composeApp/src/androidMain/assets/xray_model.tflite"
    try:
        os.makedirs(os.path.dirname(android_assets_path), exist_ok=True)
        with open(android_assets_path, 'wb') as f:
            f.write(tflite_quant_model)
        print(f"✓ Successfully auto-deployed TFLite model directly to Android assets:\n  -> {android_assets_path}")
    except Exception as e:
        print(f"Note: Could not auto-copy to Android assets: {e}")

    original_size = os.path.getsize(h5_model_path) / (1024 * 1024)
    compressed_size = os.path.getsize(output_tflite_path) / (1024 * 1024)

    print("\n" + "="*40)
    print("✓ Model conversion successful!")
    print(f"Original H5 Size:   {original_size:.2f} MB")
    print(f"Quantized TFLite:   {compressed_size:.2f} MB")
    print(f"Size Reduction:     {((original_size - compressed_size)/original_size)*100:.1f}% smaller!")
    print("="*40)

if __name__ == "__main__":
    # paths
    h5_path = "models/pretrained_model.h5"
    tflite_path = "models/xray_model.tflite"

    convert_h5_to_tflite(h5_path, tflite_path)
