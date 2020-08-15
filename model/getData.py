import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import numpy as np
import pathlib
import matplotlib.pyplot as plt

DATA_DIR = pathlib.Path('data') / 'dataset'
IMG_HEIGHT = 256
IMG_WIDTH = 256

def loadDataset(batchSize):
    # load dataset, seperate to train and validation.
    datagen = ImageDataGenerator(rotation_range=20, width_shift_range=0.1,
                                       height_shift_range=0.1, zoom_range=0.1,
                                       channel_shift_range=0.0,
                                       fill_mode='nearest', cval=0.0, horizontal_flip=True, vertical_flip=False,
                                       rescale=1 / 255.,
                                       data_format='channels_last', validation_split=0.1,
                                       dtype='float32')
    """val_datagen = ImageDataGenerator(rescale=1 / 255.,
                                     data_format='channels_last', validation_split=0.3,
                                     dtype='float32')"""
    train_generator = datagen.flow_from_directory(str(DATA_DIR), target_size=(256, 256), color_mode="rgb",
                                                        class_mode="categorical", batch_size=batchSize, subset="training")

    val_generator = datagen.flow_from_directory(str(DATA_DIR), target_size=(256, 256), color_mode="rgb",
                                                    class_mode="categorical", batch_size=batchSize, subset="validation")

    return train_generator, val_generator

def plotImages(images_arr):
    fig, axes = plt.subplots(2, 5, figsize=(20,20))
    axes = axes.flatten()
    for img, ax in zip( images_arr, axes):
        ax.imshow(img)
        ax.axis('off')
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    trainGen, valGen = loadDataset(10)
    print("dataset loaded!")
    images, labels = next(trainGen)
    print(str(labels))
    plotImages(images)
