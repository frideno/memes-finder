import tensorflow as tf

loaded = tf.keras.models.load_model("currModel")
converter = tf.lite.TFLiteConverter.from_keras_model(loaded)
tflite_model = converter.convert()
open("memeClassifierV4.tflite", "wb").write(tflite_model)
