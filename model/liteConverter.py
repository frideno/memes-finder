import tensorflow as tf
import sys

loaded = tf.keras.models.load_model(sys.argv[1])
converter = tf.lite.TFLiteConverter.from_keras_model(loaded)
tflite_model = converter.convert()
open(sys.argv[2], "wb").write(tflite_model)
