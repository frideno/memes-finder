import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import sklearn
import keras
import numpy as np
import pandas as pd
import pathlib
import os

DATASET_DIR = pathlib.Path('data') / 'dataset'
TRAIN_DIR = DATASET_DIR

def load_dataset():
    # load dataset, seperate to train and validation.
    train_datagen = ImageDataGenerator(rotation_range=20, width_shift_range=0.2,
                                       height_shift_range=0.3, zoom_range=0.3,
                                       channel_shift_range=0.0,
                                       fill_mode='nearest', cval=0.0, horizontal_flip=True, vertical_flip=False,
                                       rescale=1 / 255.,
                                       data_format='channels_last', validation_split=0.3,
                                       dtype='float32')
    val_datagen = ImageDataGenerator(rescale=1 / 255.,
                                     data_format='channels_last', validation_split=0.3,
                                     dtype='float32')
    train_generator = train_datagen.flow_from_directory(str(TRAIN_DIR), target_size=(256, 256), color_mode="rgb",
                                                        class_mode="categorical", batch_size=64, subset="training")

    val_generator = val_datagen.flow_from_directory(str(TRAIN_DIR), target_size=(256, 256), color_mode="rgb",
                                                    class_mode="categorical", batch_size=64, subset="validation")

    return train_generator, val_generator

def load_model():
    return
