import os
import pathlib
import pandas as pd
import hashlib

from data_scraping.download_reddit import download

DATA_DIR = pathlib.Path('data') / 'reddit'
DATA_CSV_PATH = DATA_DIR / 'dataset.csv'
DATASET_DIR = pathlib.Path('data') / 'dataset'

def collect_pictures(dataset_size):

    memes_subreddits = ['memes']
    non_memes_subreddits = ['pics', 'rarepuppers', 'carporn', 'cityporn', 'selfie', 'sports', 'cats', 'NatureIsFuckingLit', 'ps4', 'texts']

    for subreddit in memes_subreddits:
        download(subreddit, dataset_size // (2 * len(memes_subreddits)))
    for subreddit in non_memes_subreddits:
        download(subreddit, dataset_size // (2 * len(non_memes_subreddits)))

    dataframes = []
    for subreddit in os.listdir(DATA_DIR):
        if os.path.isdir(DATA_DIR / subreddit):
            dataframes.append(pd.read_csv(f'{DATA_DIR / subreddit}/images.csv', index_col=None))

    pd.concat(dataframes, ignore_index=True).to_csv(f'{DATA_DIR}/dataset.csv', index=True)


def reorder_directories():
    # open dataset description csv:
    dataset_desc = pd.read_csv(DATA_CSV_PATH)

    # seperate to train and test:
    classes = ['Yes', 'No']
    for cls in classes:
        os.system(f'mkdir -p {DATASET_DIR / cls}')
        file_paths = dataset_desc[dataset_desc['Memes?'] == cls]['Image Path']
        for file_path in file_paths:
            _, img_type = os.path.splitext(file_path)
            os.system(f'cp {file_path} {DATASET_DIR / cls / (str(hashlib.sha256(str(file_path).encode()).hexdigest()) + img_type)}')



collect_pictures(1000)
reorder_directories()