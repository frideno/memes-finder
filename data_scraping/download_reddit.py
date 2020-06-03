import pathlib
import random
import hashlib
import json
import praw
import argparse
import urllib
import os
import pandas as pd


CREDS = json.load(open('.credentials.json'))


class RedditCollector:
    def __init__(self, client_id, client_secret, user_agent, subreddit_name, limit):

        self.client_id = client_id
        self.client_secret = client_secret
        self.user_agent = user_agent
        self.subreddit = subreddit_name
        self.limit = limit
        self.is_subreddit_memes_related = ('meme' in self.subreddit.lower() or 'dank' in self.subreddit.lower())
        self.reddit = praw.Reddit(client_id=self.client_id, client_secret=self.client_secret,
                                  user_agent=self.user_agent, username=None, password=None)

    def collect_data(self):
        allowed_image_extensions = ['.jpg', '.jpeg', '.png']
        self.image_urls = []

        subreddit = self.reddit.subreddit(self.subreddit)
        posts = subreddit.hot(limit=self.limit)

        for post in posts:
            _, ext = os.path.splitext(post.url)

            if 'i.redd.it' in post.url and ext in allowed_image_extensions:
                self.image_urls.append(post.url)

    def save_data(self):
        dirpath = os.path.join('data', 'reddit', self.subreddit)

        if len(self.image_urls) > 0:
            if not os.path.exists(dirpath):
                os.system(f'mkdir -p {dirpath}')

        for index, url in enumerate(self.image_urls):
            try:
                image_name = url.split('/')[-1]
                print(f'downloading {url}')
                urllib.request.urlretrieve(url.replace('https', 'http'), os.path.join(dirpath, image_name))
            except Exception as ex:
                print(ex)
                print('>>> something went wrong while downloading ', url)

    def export_to_csv(self):

        if len(self.image_urls) > 0:
            images_path = os.path.join('data', 'reddit', self.subreddit, 'images.csv')
            dataframe = pd.DataFrame({
                'Image Path': [f'{os.path.join("data", "reddit", self.subreddit, url.split("/")[-1])}' for url in self.image_urls],
                'Url': self.image_urls,
                'Subject': [self.subreddit for i in range(len(self.image_urls))],
                'Memes?': ['Yes' if self.is_subreddit_memes_related else 'No' for i in range(len(self.image_urls))]
            })
            csv = dataframe.to_csv(images_path, index=None, header=True)


def download(term, count):
    data_collector = RedditCollector(client_id=CREDS['reddit_clientid'],
                                     client_secret=CREDS['reddit_secret'],
                                     user_agent=CREDS['reddit_secret'],
                                     subreddit_name=term,
                                     limit=count)

    data_collector.collect_data()
    data_collector.save_data()
    data_collector.export_to_csv()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Download from google photos')
    parser.add_argument('-n', '--count', dest='count', type=int, required=True, help='number of photos to download.')
    parser.add_argument('-s', '--subreddit', dest='subreddit', required=True, help='term to search.')

    args = parser.parse_args()

    download(args.subreddit, args.count)
