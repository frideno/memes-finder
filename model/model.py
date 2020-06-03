import sklearn
# import keras
import numpy as np
import pandas as pd
import pathlib
import os

DATASET_DIR = pathlib.Path('data') / 'dataset'

def load_dataset():
    return